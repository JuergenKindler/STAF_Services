package org.jki.staf.service.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * A default error handler doing all the typical error handling.
 * (TODO: Still have to see whether this needs improvement ...)
 */
public class DefaultErrorHandler {
	private static String CR = System.getProperty("line.separator");
	private DateFormat df;
	private String service;

	/**
	 * Create an error handler for a service.
	 * @param serviceName - the name of the service.
	 */
	public DefaultErrorHandler(String serviceName) {
		super();
		df = SimpleDateFormat.getDateTimeInstance();
	}
	
	/**
	 * Handle an exception with initialization info
	 * @param initInfo - the service initialization info
	 * @param exception - the exception
	 * @return A staf result.
	 */
	public STAFResult handleException(InitInfo initInfo, Exception exception) {
		log(createErrorMessage(exception.getClass(), initInfo.name + " " + initInfo.parms), exception);
		return createResultFromThrowable(exception);
	}
	
	/**
	 * Handle an exception that happened during request handling.
	 * @param reqInfo - info about request
	 * @param exception - the exception
	 * @return A staf result.
	 */
	public STAFResult handleException(RequestInfo reqInfo, Exception exception) {
		log(createErrorMessage(exception.getClass(), reqInfo.request), exception);
		return createResultFromThrowable(exception);
	}
	
	/**
	 * Handle an error that happened during request handling.
	 * @param reqInfo - info about request
	 * @param error - the exception
	 * @return A staf result.
	 */
	public STAFResult handleError(RequestInfo reqInfo, Error error) {
		log(createErrorMessage(error.getClass(), reqInfo.request), error);
		return createResultFromThrowable(error);
	}
	
	private void log(String message, Throwable thr) {
		System.out.println(df.format(new Date()) + " : " + message + toStacktrace(thr) + CR);
	}

	private String toStacktrace(Throwable thr) {
		StringWriter sr = new StringWriter();
		thr.printStackTrace(new PrintWriter(sr));
		return sr.toString();
	}

	private String createErrorMessage(@SuppressWarnings("rawtypes") Class errorType, String request) {
		return errorType.getSimpleName() + " on " + service + " request: " + request + CR;
	}
	
	private STAFResult createResultFromThrowable(Throwable thr) {
		STAFResult result;

		if (thr.getMessage() != null) {
			result = new STAFResult(STAFResult.JavaError, thr.getMessage() + CR + toStacktrace(thr));
		} else {
			result = new STAFResult(STAFResult.JavaError, toStacktrace(thr));
		}
		
		return result;
	}
}
