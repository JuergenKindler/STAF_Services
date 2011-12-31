package org.jki.staf.service.jmx.commands;

import java.util.Iterator;
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

public class QueryServerCommand extends AbstractServiceCommand implements
		ServiceCommand {
	private static final String PROVIDER = "Provider";
	private static final String ID = "ID";
	private static final String DISPLAY_NAME = "DisplayName";
	private VMInfo vms;
	private STAFMapClassDefinition resultMapModel;

	public QueryServerCommand(String commandName, String machineName,
			InitInfo initInfo, VMInfo vms) {
		super(commandName, machineName, initInfo);
		this.vms = vms;
		this.resultMapModel = new STAFMapClassDefinition(
				"STAF/Service/JMX/QueryServer");
		this.resultMapModel.addKey(ID, "VM identifier");
		this.resultMapModel.addKey(DISPLAY_NAME, "Display name");
		this.resultMapModel.addKey(PROVIDER, "Attached provider");
	}

	@Override
	public STAFResult execute(RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);

		if (result.rc == STAFResult.Ok) {
			String requestedID = parseResult.optionValue(ID, 1);
			List<VirtualMachineDescriptor> vmList = vms.getVMDescriptors();

			result.rc = ReturnCode.ServerNotFound.getRC();
			result.result = ReturnCode.ServerNotFound.getMessage() + " " + ID
					+ " = " + requestedID;

			for (Iterator<VirtualMachineDescriptor> vmdi = vmList.iterator(); vmdi
					.hasNext() && (result.rc != STAFResult.Ok);) {
				VirtualMachineDescriptor vmd = vmdi.next();

				if (requestedID.equals(vmd.id())) {
					result = createMachineResult(vmd);
				}
			}
		}

		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private STAFResult createMachineResult(VirtualMachineDescriptor vmd) {
		STAFMarshallingContext mc = new STAFMarshallingContext();
		mc.setMapClassDefinition(resultMapModel);
		Map resultMap = resultMapModel.createInstance();
		resultMap.put(ID, vmd.id());
		resultMap.put(DISPLAY_NAME, vmd.displayName());
		resultMap.put(PROVIDER, vmd.provider().name() + " ("
				+ vmd.provider().type() + ")");
		mc.setRootObject(resultMap);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}

	@Override
	public String getCommandHelp() {
		return getCommandName() + " " + ID + " <VirtualMachineID>";
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
		parser.addOption(ID, 0, STAFCommandParser.VALUEREQUIRED);
	}
}
