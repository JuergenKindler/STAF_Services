package org.jki.staf.service.jmx.commands;

import java.util.ArrayList;
import java.util.List;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import org.jki.staf.service.commands.AbstractServiceCommand;
import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.jmx.vmtools.VMInfo;

import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class ListLocalServers extends AbstractServiceCommand implements
		ServiceCommand {

	private static final String DISPLAY_NAME = "DISPLAY_NAME";
	private VMInfo vms;

	public ListLocalServers(String commandName, String machineName,
			InitInfo initInfo, VMInfo vms) {
		super(commandName, machineName, initInfo);
		this.vms = vms;
	}

	/** {@inheritDoc} */
	@Override
	public STAFResult execute(final RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);

		if (result.rc == STAFResult.Ok) {
			List<String> l = (parseResult.optionTimes(DISPLAY_NAME) > 0) ? 
					vms.getVmDisplayNames() : vms.getVmIds();
			String marshalledList = STAFMarshallingContext.marshall(l, null);
			result.result = marshalledList;
		}

		return result;
	}

	@Override
	public String getCommandHelp() {
		return getCommandName() + " [" + DISPLAY_NAME + "]";
	}

	@Override
	protected int getTrustLevel() {
		return 4;
	}

	/**
	 * Sets up the command parser.
	 */
	protected void setupParser() {
		super.setupParser();
		parser.addOption(DISPLAY_NAME, 0, STAFCommandParser.VALUENOTALLOWED);
	}
}
