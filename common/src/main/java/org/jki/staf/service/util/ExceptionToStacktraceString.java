package org.jki.staf.service.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Small helper to convert an Exception to a stack trace string
 * @author jkindler
 */
public class ExceptionToStacktraceString {
	/**
	 * Convert a throwable / exception to a stacktrace string.
	 * @param throwable - the throwable
	 * @return a string containing the stack trace.
	 */
	public static String toStacktrace(Throwable throwable) {
		StringWriter sr = new StringWriter();
		throwable.printStackTrace(new PrintWriter(sr));
		return sr.toString();
	}
}
