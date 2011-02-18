package org.jki.staf.service.irc.commands;

import org.jki.staf.service.irc.ServiceCommand;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;
import org.jki.staf.service.irc.util.VersionReader;

/**
 * A command to return the version of the service.
 */
public class VersionCommand extends AbstractServiceCommand implements ServiceCommand {
	private VersionReader reader;
	
	public VersionCommand(String commandName, String machineName, InitInfo initInfo) {
		super(commandName, machineName, initInfo);
		reader = new VersionReader();
	}
	
	@Override
	public STAFResult execute(final RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);
		
		if (result.rc == STAFResult.Ok) {
			result = new STAFResult(STAFResult.Ok, reader.getVersion());
		}
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.jki.staf.service.irc.commands.AbstractServiceCommand#getTrustLevel()
	 */
	@Override
	protected int getTrustLevel() {
		return 1;
	}
}
