package org.dieschnittstelle.ess.ser;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ObjectOutputStream;

import static org.dieschnittstelle.ess.utils.Utils.show;

@WebServlet(urlPatterns = "/api/async/touchpoints", asyncSupported = true)
public class TouchpointServiceServletAsync extends HttpServlet {

	protected static Logger logger = org.apache.logging.log4j.LogManager
			.getLogger(TouchpointServiceServletAsync.class);

	public TouchpointServiceServletAsync() {
		show("TouchpointServiceServlet: constructor invoked\n");
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {

		logger.info("doGet()");

		// we need to check whether we are already running in an async context or whether a new one needs to be created
		// in any case, dispatching needs to use the dispatch() method on the AsyncContext object rather than the request dispatcher
		AsyncContext asyncContext = !request.isAsyncStarted()
				? request.startAsync()
				: request.getAsyncContext();

		new Thread(()->{
			logger.info("doGet(): sleeping...");
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("doGet(): continuing...");
			try {
				asyncContext.dispatch("/api/touchpoints");
			}
			catch (Exception e) {
				response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			}
		}).start();

	}
	
	/*
	@Override	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {

		// assume POST will only be used for touchpoint creation, i.e. there is
		// no need to check the uri that has been used

		// obtain the executor for reading out the touchpoints from the servlet context using the touchpointCRUD attribute

		try {
			// create an ObjectInputStream from the request's input stream
		
			// read an AbstractTouchpoint object from the stream
		
			// call the create method on the executor and take its return value
		
			// set the response status as successful, using the appropriate
			// constant from HttpServletResponse
		
			// then write the object to the response's output stream, using a
			// wrapping ObjectOutputStream
		
			// ... and write the object to the stream
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	*/


	
}
