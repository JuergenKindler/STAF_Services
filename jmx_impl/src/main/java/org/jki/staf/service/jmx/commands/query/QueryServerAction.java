package org.jki.staf.service.jmx.commands.query;

import java.util.Map;
import java.util.logging.Logger;

import org.jki.staf.service.ReturnCode;
import org.jki.staf.service.commands.CommandAction;
import org.jki.staf.service.jmx.commands.QueryCommand;
import org.jki.staf.service.jmx.commands.Constants;
import org.jki.staf.service.jmx.vmtools.VMInfo;

import com.ibm.staf.STAFMapClassDefinition;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @author jkindler
 */
public class QueryServerAction implements CommandAction {
	private static final Logger LOG = Logger.getLogger(QueryCommand.class.getSimpleName());
	private VMInfo vms;
	private STAFMapClassDefinition resultMapModel;

	/**
	 * Create a command to query a local jmx server
	 * 
	 * @param vms - the virtual machine info
	 */
	public QueryServerAction(VMInfo vms) {
		this.vms = vms;
		this.resultMapModel = new STAFMapClassDefinition("STAF/Service/JMX/QueryServer");
		this.resultMapModel.addKey(Constants.VMID, Constants.T_VIRTUAL_MACHINE_ID);
		this.resultMapModel.addKey(Constants.DISPLAY_NAME, Constants.T_DISPLAY_NAME);
		this.resultMapModel.addKey(Constants.PROVIDER, Constants.T_PROVIDER);
	}

	/** {@inheritDoc} */
	public STAFResult execute(STAFCommandParseResult parseResult) {
		STAFResult result = new STAFResult(STAFResult.Ok);
		// Get the VM for the ID ...
		String requestedVMID = parseResult.optionValue(Constants.VMID, 1);
		result.rc = ReturnCode.ServerNotFound.getRC();
		result.result = ReturnCode.ServerNotFound.getMessage() + " " + Constants.VMID + " = " + requestedVMID;
		VirtualMachineDescriptor vmd = vms.getVmd(requestedVMID);

		return (vmd != null) ? createMachineResult(vmd) : result;
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
		return Constants.SERVER + " " + Constants.VMID + " <" + Constants.T_VIRTUAL_MACHINE_ID + ">";
	}
}
