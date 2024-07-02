package org.dieschnittstelle.ess.utils.interceptors;

import org.apache.logging.log4j.Logger;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// TODO MP: use interceptor, see: https://tomee.apache.org/examples-trunk/cdi-interceptors/
@Interceptor
@Logged
public class LoggedInterceptor {

	protected static Logger logger = org.apache.logging.log4j.LogManager.getLogger(LoggedInterceptor.class);

	/*
	 * a map of loggers
	 */
	private static Map<String, Logger> loggers = Collections.synchronizedMap(new HashMap<String, Logger>());

	/**
	 * obtain a logger
	 */
	private Logger getLogger(Class<?> klass) {
		String key = getLoggingKlassnameForKlass(klass);
		if (loggers.containsKey(key))
			return loggers.get(key);
		return createNewLoggerForKlassname(key);
	}

	private String getLoggingKlassnameForKlass(Class<?> klass) {
		String klassname = klass.getSimpleName();
		if (klassname.indexOf("$") != -1) {
			klassname = klassname.substring(0,klassname.indexOf("$"));
		}
		return klassname;
	}

	private synchronized Logger createNewLoggerForKlassname(String klassname) {
		logger.info("createNewLogger(): classname is: " + klassname);

		Logger logger = org.apache.logging.log4j.LogManager.getLogger(klassname);
		loggers.put(klassname, logger);

		return logger;
	}

	/**
	 * log a method invocation
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@AroundInvoke
	public Object logMethod(final InvocationContext context) throws Exception {

		StringBuffer buf = new StringBuffer();
		String prefix = context.getMethod().getName() + "()";

		/*
		 * log the input
		 */
		buf.append(prefix);
		buf.append(": invoke with arguments: (");

		for (int i = 0; i < context.getParameters().length; i++) {
			buf.append(context.getParameters()[i]);
			if (i < (context.getParameters().length - 1)) {
				buf.append(", ");
			}
		}
		buf.append(")");

		getLogger(context.getTarget().getClass()).info(buf.toString());

		/*
		 * execute the method
		 */
		try {
			Object result = context.proceed();

			/*
			 * log the output
			 */
			buf.setLength(0);
			buf.append(prefix);

			// check whether we have a void method
			if (context.getMethod().getReturnType() == Void.TYPE) {
				buf.append(": returned");
			} else {
				buf.append(": got return value: ");
				buf.append(result);
			}

			getLogger(context.getTarget().getClass()).info(buf.toString());

			return result;
		}
		catch (Exception e) {
			getLogger(context.getTarget().getClass()).error(prefix + ": got exception: " + e,e);
			throw e;
		}
	}

}
