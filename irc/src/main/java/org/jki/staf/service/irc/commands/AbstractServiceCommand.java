package org.jki.staf.service.irc.commands;

import org.jki.staf.service.irc.ServiceCommand;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public abstract class AbstractServiceCommand implements ServiceCommand {
	protected String name;
	protected String localMachineName;
	protected InitInfo info;
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


	/* (non-Javadoc)
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

	
	/* (non-Javadoc)
	 * @see org.jki.staf.service.irc.ServiceCommand#getCommandName()
	 */
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
