package org.jki.staf.service.jmx.commands;

import java.util.List;
import java.util.Map;

import org.jki.staf.service.ReturnCode;
import org.jki.staf.service.commands.AbstractServiceCommand;
import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.jmx.vmtools.VMInfo;

import com.ibm.staf.STAFMapClassDefinition;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @author jkindler
 */
public class QueryServerCommand extends AbstractServiceCommand implements ServiceCommand {
	private VMInfo vms;
	private STAFMapClassDefinition resultMapModel;

	/**
	 * Create a command to query all local jmx servers
	 * 
	 * @param commandName
	 *            - the command name in staf
	 * @param machineName
	 *            - the name of the running machine
	 * @param initInfo
	 *            - staf internal initialization info
	 * @param vms
	 *            - the virtual machine info
	 */
	public QueryServerCommand(String commandName, String machineName, InitInfo initInfo, VMInfo vms) {
		super(commandName, machineName, initInfo);
		this.vms = vms;
		this.resultMapModel = new STAFMapClassDefinition("STAF/Service/JMX/QueryServer");
		this.resultMapModel.addKey(Constants.VMID, Constants.T_VIRTUAL_MACHINE_ID);
		this.resultMapModel.addKey(Constants.DISPLAY_NAME, Constants.T_DISPLAY_NAME);
		this.resultMapModel.addKey(Constants.PROVIDER, Constants.T_PROVIDER);
	}

	/** {@inheritDoc} */
	@Override
	public STAFResult execute(RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);

		if (result.rc == STAFResult.Ok) {
			String requestedID = parseResult.optionValue(Constants.VMID, 1);
			List<VirtualMachineDescriptor> vmList = vms.getVMDescriptors();

			result.rc = ReturnCode.ServerNotFound.getRC();
			result.result = ReturnCode.ServerNotFound.getMessage() + " " + Constants.VMID + " = " + requestedID;

			VirtualMachineDescriptor vmd = vms.getVmd(requestedID);
			result = (vmd != null) ? createMachineResult(vmd) : result;
		}

		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private STAFResult createMachineResult(VirtualMachineDescriptor vmd) {
		STAFMarshallingContext mc = new STAFMarshallingContext();
		mc.setMapClassDefinition(resultMapModel);
		Map resultMap = resultMapModel.createInstance();
		resultMap.put(Constants.VMID, vmd.id());
		resultMap.put(Constants.DISPLAY_NAME, vmd.displayName());
		resultMap.put(Constants.PROVIDER, vmd.provider().name() + " (" + vmd.provider().type() + ")");
		mc.setRootObject(resultMap);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}

	/** {@inheritDoc} */
	@Override
	public String getCommandHelp() {
		return getCommandName() + " " + Constants.VMID + " <" + Constants.T_VIRTUAL_MACHINE_ID + ">";
	}

	/** {@inheritDoc} */
	@Override
	protected int getTrustLevel() {
		return 4;
	}

	/**
	 * Sets up the command parser.
	 */
	protected void setupParser() {
		super.setupParser();
		parser.addOption(Constants.VMID, 0, STAFCommandParser.VALUEREQUIRED);
	}
}
