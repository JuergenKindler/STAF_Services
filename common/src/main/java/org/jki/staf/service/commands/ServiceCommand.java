package org.jki.staf.service.commands;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * A staf command.
 */
public interface ServiceCommand {
	/**
	 * List of constants for standard commands.
	 */
	public static final String ADD = "ADD";
	public static final String CREATE = "CREATE";
	public static final String REMOVE = "REMOVE";
	public static final String DELETE = "DELETE";
	public static final String LIST = "LIST";
	public static final String QUERY = "QUERY";
	public static final String REQUEST = "REQUEST";
	public static final String RELEASE = "RELEASE";
	public static final String GET = "GET";
	public static final String SET = "SET";
	public static final String HELP = "HELP";
	public static final String VERSION = "VERSION";

	/**
	 * Execute a request and return a STAF result.
	 * @param reqInfo - the request
	 * @return a STAF Result instance
	 */
	STAFResult execute(RequestInfo reqInfo);

	/**
	 * Return the name of the command
	 * @return the command name
	 */
	String getCommandName();

	/**
	 * Return the help string of the command
	 * @return the help
	 */
	String getCommandHelp();
}
