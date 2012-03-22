package org.jki.staf.service.jmx.vmtools;

import org.jki.staf.service.ReturnCode;

/**
 * Exception when creation of MBean server connection fails
 * @author jkindler
 */
public class MBeanServerConnectionException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReturnCode code;

	/**
	 * Create an exception based on STAF status code.
	 * @param code - the status code
	 */
	public MBeanServerConnectionException(ReturnCode code) {
		super(code.getMessage());
		this.code = code;
	}

	/**
	 * Create an exception based on STAF status code.
	 * @param cause - a root cause for the exception
	 * @param code - the status code
	 */
	public MBeanServerConnectionException(Throwable cause, ReturnCode code) {
		super(code.getMessage(), cause);
		this.code = code;
	}
	
	/**
	 * @return the Return code
	 */
	public ReturnCode getCode() {
		return code;
	}
}
