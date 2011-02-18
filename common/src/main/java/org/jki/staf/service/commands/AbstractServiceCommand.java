package org.jki.staf.service.commands;

import org.jki.staf.service.commands.ServiceCommand;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * An abstract service command that provides a basic implementation of a service command.
 */
public abstract class AbstractServiceCommand implements ServiceCommand {
	/**
	 * The name of the command
	 */
	protected String name;

	/**
	 * The name of the local machine (where the service is installed)
	 */
	protected String localMachineName;

	/**
	 * Information from the initialization of the service
	 */
	protected InitInfo info;

	/**
	 * The command parser for this command. 
	 * Necessary to parse all options from command line.
	 */
	protected STAFCommandParser parser;
	
	private AbstractServiceCommand() {
		super();
	}


	/**
	 * Create a new command.
	 * @param commandName - the name of the command.
	 * @param machineName - the local machine name.
	 * @param initInfo - the service initialization info.
	 */
	public AbstractServiceCommand(final String commandName, final String machineName, final InitInfo initInfo) {
		this();
		name = commandName;
		localMachineName = machineName;
		info = initInfo;
		setupParser();
	}


	/** (non-Javadoc)
	 * @see org.jki.staf.service.irc.ServiceCommand#execute(com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo)
	 */
	@Override
	public STAFResult execute(final RequestInfo reqInfo) {
		STAFResult result = new STAFResult(STAFResult.Ok);
		
		STAFResult trustResult = 
			STAFUtil.validateTrust(getTrustLevel(), info.name, name, localMachineName, reqInfo);

		if (trustResult.rc != STAFResult.Ok) {
			result = trustResult;
		}
	
		return result;
	}


	@Override
	public final String getCommandName() {
		return name;
	}


	/**
	 * Returns the trust level required to execute this command.
	 * @return the trust level.
	 */
	protected abstract int getTrustLevel();


	/**
	 * Returns the name of the local machine (where the service is running).
	 * @return the local machine name
	 */
	protected final String getLocalMachineName() {
		return localMachineName;
	}


	/**
	 * Sets up the command parser.
	 */
	protected void setupParser() {
		parser = new STAFCommandParser(0, false);
		parser.addOption(getCommandName(), 1, STAFCommandParser.VALUENOTALLOWED); 
	}
}
