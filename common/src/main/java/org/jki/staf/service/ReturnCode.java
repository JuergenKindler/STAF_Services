package org.jki.staf.service;

/**
 * @author jkindler
 *
 */
public enum ReturnCode {
	/**
	 * Could not find the JMX server. Possibly the process terminated in the mean time?
	 */
	ServerNotFound(70, "The server could not be found"),

	/**
	 * Could attach to JMX server.
	 */
	ServerAttachFailed(71, "The server could not be attached"),

	/**
	 * Could attach to JMX server.
	 */
	ServerCommunicationError(72, "Server communication failed"),
	
	/**
	 * Could not find connector address for local connection in server properties
	 */
	ServerHasNoConnectorAddress(73, "The server has not local connector address"),
	
	/**
	 * The ObjectID of the MBean is invalid
	 */
	MBeanObjectIDInvalid(74, "The invalid MBean object ID"),
	
	/**
	 * No MBean with this object ID
	 */
	MBeanDoesNotExist(75, "No MBean with this object ID"),
	
	/**
	 * The attribute name does not exist for this MBean
	 */
	MBeanAttributeDoesNotExist(76, "MBean attribute does not exist")
	;
	
	private int rc;
	private String message;
	
	private ReturnCode(int rc, String message) {
		this.rc = rc;
		this.message = message;
	}
	
	/**
	 * @return the error code
	 */
	public int getRC() {
		return rc;
	}
	
	/**
	 * @return the error message
	 */
	public String getMessage() {
		return message;
	}
	
    /** {@inheritDoc}*/
	@Override
	public String toString() {
		return "" + rc + " - " + message;
	}
}
