package org.jki.staf.service.irc.commands;

import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.commands.AbstractServiceCommand;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * Implement help for the irc service.
 */
public class HelpCommand extends AbstractServiceCommand implements ServiceCommand {
	private String helpString;

	/**
	 * Create a help command instance.
	 * @param commandName - the name of this command
	 * @param machineName - the name of the local machine
	 * @param initInfo - initialization info of the service
	 * @param help - the help string
	 */
	public HelpCommand(final String commandName, final String machineName, final InitInfo initInfo, final String help) {
		super(commandName, machineName, initInfo);
		helpString = help;
	}

    /** {@inheritDoc}*/
	@Override
	public STAFResult execute(RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);
		
		if (result.rc == STAFResult.Ok) {
			result = new STAFResult(STAFResult.Ok, helpString);
		}
		
		return result;
	}

	/**
	 * Returns the help string.
	 * @return the help
	 */
	public String getHelpString() {
		return helpString;
	}

    /** {@inheritDoc}*/
	@Override
	protected int getTrustLevel() {
		return 1;
	}
}
