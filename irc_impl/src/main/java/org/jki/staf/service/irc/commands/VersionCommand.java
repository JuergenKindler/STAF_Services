package org.jki.staf.service.irc.commands;

import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.commands.AbstractServiceCommand;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;
import org.jki.staf.service.util.VersionReader;

/**
 * A command to return the version of the service.
 */
public class VersionCommand extends AbstractServiceCommand implements ServiceCommand {
	private VersionReader reader;
	
	/**
	 * Create a version command instance.
	 * @param commandName - the name of the command
	 * @param machineName - the local machine name
	 * @param initInfo - the initialization info of the service
	 */
	public VersionCommand(String commandName, String machineName, InitInfo initInfo) {
		super(commandName, machineName, initInfo);
		reader = new VersionReader();
	}
	
    /** {@inheritDoc}*/
	@Override
	public STAFResult execute(final RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);
		
		if (result.rc == STAFResult.Ok) {
			result = new STAFResult(STAFResult.Ok, reader.getVersion());
		}
		
		return result;
	}

	
    /** {@inheritDoc}*/
	@Override
	protected int getTrustLevel() {
		return 1;
	}
}
