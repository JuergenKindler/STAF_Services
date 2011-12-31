package org.jki.staf.service;

public enum ReturnCode {
	ServerNotFound(70, "The server could not be found");
	
	private int rc;
	private String message;
	
	private ReturnCode(int rc, String message) {
		this.rc = rc;
		this.message = message;
	}
	
	public int getRC() {
		return rc;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String toString() {
		return "" + rc + " - " + message;
	}
}
