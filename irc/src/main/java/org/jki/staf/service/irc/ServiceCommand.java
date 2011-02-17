package org.jki.staf.service.irc;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * A staf command.
 */
public interface ServiceCommand {
	/**
	 * Execute a request and return a STAF result
	 * @param reqInfo
	 * @return a STAF Result instance
	 */
	STAFResult execute(RequestInfo reqInfo);

	/**
	 * Return the name of the command
	 * @return the command name
	 */
	String getCommandName();
}
