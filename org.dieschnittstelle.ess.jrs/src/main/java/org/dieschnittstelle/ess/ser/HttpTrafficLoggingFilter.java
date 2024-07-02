package org.dieschnittstelle.ess.ser;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.stream.Collectors;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import static org.dieschnittstelle.ess.utils.Utils.*;

import org.apache.commons.io.output.TeeOutputStream;

import org.apache.logging.log4j.Logger;

/**
 * example of a filter, taken from Crawford/Kaplan, JEE Design Patterns, 2003
 * @author joern
 *
 */

public class HttpTrafficLoggingFilter implements Filter {

	/**
	 * the logger
	 */
	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(HttpTrafficLoggingFilter.class);

	/**
	 * the config passed with the init method
	 */
	private FilterConfig config;

	/**
	 * constructor for lifecycle logging
	 */
	public HttpTrafficLoggingFilter() {
		show("HttpTrafficLoggingFilter: constructor invoked\n");
	}

	@Override
	public void destroy() {
		logger.info("destroy()");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
						 FilterChain chain) throws IOException, ServletException {

		System.out.println("\n\n%%%%%%%%%%%%%%% start request processing %%%%%%%%%%%%%%%\n");

		show("HttpTrafficLoggingFilter: doFilter() invoked\n");

		logger.info("doFilter(): " + request + ", " + response + ", " + chain);

		// obtain the servlet context
		ServletContext sc = config.getServletContext();

		// log the request
		logger.info("request is:\n" + logRequest((HttpServletRequest)request));

//		BufferedRequestWrapper bufferedReqest = new BufferedRequestWrapper((HttpServletRequest) request);
//		BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper((HttpServletResponse)response);

		// continue filtering
		chain.doFilter(request, response);

//		logger.info("request body was: " + bufferedReqest.getRequestBody());

		System.out.println("\n\n%%%%%%%%%%%%%%% request processing done %%%%%%%%%%%%%%%\n");
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.info("init(): " + config);

		this.config = config;
	}

	/**
	 * static method for logging a request
	 *
	 * this also exemplifies access to the attributes, parameters and headers of
	 * a request
	 *
	 * see: https://stackoverflow.com/questions/1528628/logging-payload-of-posts-to-tomcat
	 */
	public static String logRequest(HttpServletRequest request) {
		StringBuffer buf = new StringBuffer();


		// access the attributes
		buf.append("Request Properties:");
		buf.append("\nMethod: " + request.getMethod());
		buf.append("\nProtocol: " + request.getProtocol());
		buf.append("\nQueryString: " + request.getQueryString());
		buf.append("\nRequestURL: " + request.getRequestURL());
		buf.append("\nRequestURI: " + request.getRequestURI());
		buf.append("\nLocalName: " + request.getLocalName());
		buf.append("\nLocalAddr: " + request.getLocalAddr());
		buf.append("\nLocalPort: " + request.getLocalPort());
		buf.append("\nContextPath: " + request.getContextPath());
		buf.append("\nServletPath: " + request.getServletPath());
		buf.append("\nPathInfo: " + request.getPathInfo());
		buf.append("\nRealPath: "
				+ request.getServletContext().getRealPath(
				request.getServletPath()));
		buf.append("\nContentType: " + request.getContentType());

		buf.append("\nParameters:");

		// access the parameters
		for (Enumeration<String> e = request.getParameterNames(); e
				.hasMoreElements();) {
			String name = e.nextElement();
			String[] vals = request.getParameterValues(name);
			buf.append("\n\t" + name + "=" + Arrays.toString(vals));
		}

		// access the headers
		buf.append("\nRequest Header:");

		for (Enumeration<String> e = request.getHeaderNames(); e
				.hasMoreElements();) {
			String name = e.nextElement();
			String value = request.getHeader(name);
			buf.append("\n\t" + name + "=" + value);
		}

		buf.append("\nAttributes:");
		for (Enumeration<String> e = request.getAttributeNames(); e
				.hasMoreElements();) {
			String name = e.nextElement();
			Object value = request.getAttribute(name);
			buf.append("\n\t" + name + "=" + value + " of type "
					+ (value == null ? "<null type>" : value.getClass()));
		}

//		// read the input stream
//		try {
//			String requestBody= new BufferedReader(new InputStreamReader(request.getInputStream()))
//					.lines().collect(Collectors.joining("\n"));
//			buf.append("\n\trequestBody: " + requestBody);
//		}
//		catch (Exception e) {
//			logger.error(e.getMessage(),e);
//		}

		return buf.toString();
	}


	private static final class BufferedRequestWrapper extends
			HttpServletRequestWrapper {

		private ByteArrayInputStream bais = null;
		private ByteArrayOutputStream baos = null;
		private BufferedServletInputStream bsis = null;
		private byte[] buffer = null;

		public BufferedRequestWrapper(HttpServletRequest req)
				throws IOException {
			super(req);
			// Read InputStream and store its content in a buffer.
			InputStream is = req.getInputStream();
			this.baos = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			int letti;
			while ((letti = is.read(buf)) > 0) {
				this.baos.write(buf, 0, letti);
			}
			this.buffer = this.baos.toByteArray();
		}

		@Override
		public ServletInputStream getInputStream() {
			this.bais = new ByteArrayInputStream(this.buffer);
			this.bsis = new BufferedServletInputStream(this.bais);

			return this.bsis;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return new BufferedReader(new InputStreamReader(this.getInputStream()));
		}

		String getRequestBody() throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					this.getInputStream()));
			String line = null;
			StringBuilder inputBuffer = new StringBuilder();
			do {
				line = reader.readLine();
				if (null != line) {
					inputBuffer.append(line.trim());
				}
			} while (line != null);
			reader.close();
			return inputBuffer.toString().trim();
		}

	}

	private static final class BufferedServletInputStream extends
			ServletInputStream {

		private ByteArrayInputStream bais;

		public BufferedServletInputStream(ByteArrayInputStream bais) {
			this.bais = bais;
		}

		@Override
		public int available() {
			return this.bais.available();
		}

		@Override
		public int read() {
			return this.bais.read();
		}

		@Override
		public int read(byte[] buf, int off, int len) {
			return this.bais.read(buf, off, len);
		}

		@Override
		public boolean isFinished() {
			return false;
		}

		@Override
		public boolean isReady() {
			return false;
		}

		@Override
		public void setReadListener(ReadListener readListener) {

		}
	}

	public class TeeServletOutputStream extends ServletOutputStream {

		private final TeeOutputStream targetStream;

		public TeeServletOutputStream(OutputStream one, OutputStream two) {
			targetStream = new TeeOutputStream(one, two);
		}

		@Override
		public void write(int arg0) throws IOException {
			this.targetStream.write(arg0);
		}

		public void flush() throws IOException {
			super.flush();
			this.targetStream.flush();
		}

		public void close() throws IOException {
			super.close();
			this.targetStream.close();
		}

		@Override
		public boolean isReady() {
			return false;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {

		}
	}

	public class BufferedResponseWrapper implements HttpServletResponse {

		HttpServletResponse original;
		TeeServletOutputStream teeStream;
		PrintWriter teeWriter;
		ByteArrayOutputStream bos;

		public BufferedResponseWrapper(HttpServletResponse response) {
			original = response;
		}

		public String getContent() {
			if(bos == null){
				return null;
			}
			return bos.toString();
		}

		@Override
		public PrintWriter getWriter() throws IOException {

			if (this.teeWriter == null) {
				this.teeWriter = new PrintWriter(new OutputStreamWriter(
						getOutputStream()));
			}
			return this.teeWriter;
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {

			if (teeStream == null) {
				bos = new ByteArrayOutputStream();
				teeStream = new TeeServletOutputStream(
						original.getOutputStream(), bos);
			}
			return teeStream;
		}

		@Override
		public String getCharacterEncoding() {
			return original.getCharacterEncoding();
		}

		@Override
		public String getContentType() {
			return original.getContentType();
		}

		@Override
		public void setCharacterEncoding(String charset) {
			original.setCharacterEncoding(charset);
		}

		@Override
		public void setContentLength(int len) {
			original.setContentLength(len);
		}

		@Override
		public void setContentLengthLong(long l) {

		}

		@Override
		public void setContentType(String type) {
			original.setContentType(type);
		}

		@Override
		public void setBufferSize(int size) {
			original.setBufferSize(size);
		}

		@Override
		public int getBufferSize() {
			return original.getBufferSize();
		}

		@Override
		public void flushBuffer() throws IOException {
			if (teeStream != null) {
				teeStream.flush();
			}
			if (this.teeWriter != null) {
				this.teeWriter.flush();
			}
		}

		@Override
		public void resetBuffer() {
			original.resetBuffer();
		}

		@Override
		public boolean isCommitted() {
			return original.isCommitted();
		}

		@Override
		public void reset() {
			original.reset();
		}

		@Override
		public void setLocale(Locale loc) {
			original.setLocale(loc);
		}

		@Override
		public Locale getLocale() {
			return original.getLocale();
		}

		@Override
		public void addCookie(Cookie cookie) {
			original.addCookie(cookie);
		}

		@Override
		public boolean containsHeader(String name) {
			return original.containsHeader(name);
		}

		@Override
		public String encodeURL(String url) {
			return original.encodeURL(url);
		}

		@Override
		public String encodeRedirectURL(String url) {
			return original.encodeRedirectURL(url);
		}

		@SuppressWarnings("deprecation")
		@Override
		public String encodeUrl(String url) {
			return original.encodeUrl(url);
		}

		@SuppressWarnings("deprecation")
		@Override
		public String encodeRedirectUrl(String url) {
			return original.encodeRedirectUrl(url);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			original.sendError(sc, msg);
		}

		@Override
		public void sendError(int sc) throws IOException {
			original.sendError(sc);
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			original.sendRedirect(location);
		}

		@Override
		public void setDateHeader(String name, long date) {
			original.setDateHeader(name, date);
		}

		@Override
		public void addDateHeader(String name, long date) {
			original.addDateHeader(name, date);
		}

		@Override
		public void setHeader(String name, String value) {
			original.setHeader(name, value);
		}

		@Override
		public void addHeader(String name, String value) {
			original.addHeader(name, value);
		}

		@Override
		public void setIntHeader(String name, int value) {
			original.setIntHeader(name, value);
		}

		@Override
		public void addIntHeader(String name, int value) {
			original.addIntHeader(name, value);
		}

		@Override
		public void setStatus(int sc) {
			original.setStatus(sc);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void setStatus(int sc, String sm) {
			original.setStatus(sc, sm);
		}

		@Override
		public int getStatus() {
			return 0;
		}

		@Override
		public String getHeader(String s) {
			return null;
		}

		@Override
		public Collection<String> getHeaders(String s) {
			return null;
		}

		@Override
		public Collection<String> getHeaderNames() {
			return null;
		}

	}

}
