/**
 * 
 */
package org.jki.staf.service.jmx;

import java.util.ArrayList;
import java.util.List;

import org.jki.staf.service.GenericStafService;
import org.jki.staf.service.commands.HelpCommand;
import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.commands.VersionCommand;
import org.jki.staf.service.jmx.commands.ListLocalServers;

import com.ibm.staf.service.STAFServiceInterfaceLevel30;

/**
 * A service that allows to lookup local jmx servers communicate with them.
 */
public class JmxService extends GenericStafService implements
		STAFServiceInterfaceLevel30 {
	/**
	 * Default constructor.
	 */
	public JmxService() {
		super();
	}

	/**
	 * Create and register all commands
	 */
	protected void setupCommands() {
		commands.put(ServiceCommand.VERSION, new VersionCommand(
				ServiceCommand.VERSION, localMachineName, initInfo));

		commands.put(ServiceCommand.LIST, new ListLocalServers(
				ServiceCommand.LIST, localMachineName, initInfo));

		List<ServiceCommand> helpCommands = new ArrayList<ServiceCommand>();
		for (ServiceCommand cmd : commands.values()) {
			helpCommands.add(cmd);
		}

		commands.put(ServiceCommand.HELP, new HelpCommand(ServiceCommand.HELP,
				localMachineName, initInfo, helpCommands));
	}
}
