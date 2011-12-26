package org.jki.staf.service.commands;

import java.util.List;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * Implement help for the irc service.
 */
public class HelpCommand extends AbstractServiceCommand implements
		ServiceCommand {
	private List<ServiceCommand> commands;
	
	/**
	 * Create a help command instance.
	 * 
	 * @param commandName - the name of this command
	 * @param machineName - the name of the local machine
	 * @param initInfo    - initialization info of the service
	 * @param commands    - a list of service commands that contribute to this help
	 */
	public HelpCommand(final String commandName, final String machineName,
			final InitInfo initInfo, final List<ServiceCommand> commands) {
		super(commandName, machineName, initInfo);
		this.commands = commands;
	}

	/** {@inheritDoc} */
	@Override
	public STAFResult execute(RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);

		if (result.rc == STAFResult.Ok) {
			result = new STAFResult(STAFResult.Ok, getCommandHelp());
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected int getTrustLevel() {
		return 1;
	}

	@Override
	public String getCommandHelp() {
		StringBuffer help = new StringBuffer();
		for (ServiceCommand cmd : commands) {
			help.append(cmd.getCommandHelp());
			help.append("\n\n");
		}
		help.append("HELP");
		return help.toString();
	}
}
