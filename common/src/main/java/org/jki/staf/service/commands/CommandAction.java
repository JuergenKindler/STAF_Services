package org.jki.staf.service.commands;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;

/**
 * A staf command action executed by a complex command.
 */
public interface CommandAction {
	/**
	 * Execute a request and return a STAF result.
	 * @param parseResult - the parsed request
	 * @return a STAF Result instance
	 */
	STAFResult execute(STAFCommandParseResult parseResult);

	/**
	 * Return the help string of the command
	 * @return the help
	 */
	String getCommandHelp();
}
