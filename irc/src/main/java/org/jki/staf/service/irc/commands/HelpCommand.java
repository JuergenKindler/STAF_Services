package org.jki.staf.service.irc.commands;

import org.jki.staf.service.irc.ServiceCommand;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * Implement help for the irc service.
 */
public class HelpCommand extends AbstractServiceCommand implements ServiceCommand {
	private String helpString;

	public HelpCommand(final String commandName, final String machineName, final InitInfo initInfo, final String help) {
		super(commandName, machineName, initInfo);
		helpString = help;
	}

	/* (non-Javadoc)
	 * @see org.jki.staf.service.irc.commands.AbstractServiceCommand#execute(com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo)
	 */
	public STAFResult execute(RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);
		
		if (result.rc == STAFResult.Ok) {
			result = new STAFResult(STAFResult.Ok, helpString);
		}
		
		return result;
	}

	public String getHelpString() {
		return helpString;
	}
	
	@Override
	protected int getTrustLevel() {
		return 1;
	}
}
