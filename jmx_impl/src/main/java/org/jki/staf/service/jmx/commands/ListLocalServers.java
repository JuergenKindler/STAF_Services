package org.jki.staf.service.jmx.commands;

import java.util.ArrayList;
import java.util.List;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import org.jki.staf.service.commands.AbstractServiceCommand;
import org.jki.staf.service.commands.ServiceCommand;

import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class ListLocalServers extends AbstractServiceCommand implements
		ServiceCommand {

	private static final String DISPLAY_NAME = "DISPLAY_NAME";


	public ListLocalServers(String commandName, String machineName,
			InitInfo initInfo) {
		super(commandName, machineName, initInfo);
	}


	/** {@inheritDoc}*/
	@Override
	public STAFResult execute(final RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);
		List<String> idList = new ArrayList<String>();
		
		if (result.rc == STAFResult.Ok) {
			List<VirtualMachineDescriptor> list = VirtualMachine.list();
			
			for (VirtualMachineDescriptor vm : list) {
				idList.add((parseResult.optionTimes(DISPLAY_NAME) > 0) ? vm.displayName() : vm.id());
			}
			String marshalledList = STAFMarshallingContext.marshall(idList, null);
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
		return 5;
	}


	/**
	 * Sets up the command parser.
	 */
	protected void setupParser() {
		super.setupParser();
		parser.addOption(DISPLAY_NAME, 0, STAFCommandParser.VALUENOTALLOWED); 
	}
}
