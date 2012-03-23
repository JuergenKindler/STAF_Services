package org.jki.staf.service.jmx.commands.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jki.staf.service.ReturnCode;
import org.jki.staf.service.commands.CommandAction;
import org.jki.staf.service.jmx.commands.ListCommand;
import org.jki.staf.service.jmx.commands.Constants;
import org.jki.staf.service.jmx.vmtools.MBeanServerConnectionException;
import org.jki.staf.service.jmx.vmtools.MBeanServerConnector;
import org.jki.staf.service.jmx.vmtools.VMInfo;
import org.jki.staf.service.util.ExceptionToStacktraceString;

import com.ibm.staf.STAFMapClassDefinition;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @author jkindler
 */
public class ListObjectsAction implements CommandAction {
	private static final Logger LOG = Logger.getLogger(ListCommand.class.getSimpleName());
	private VMInfo vms;
	private STAFMapClassDefinition resultMapModel;

	/**
	 * Create a command to list all objects names of a VM
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
	public ListObjectsAction(VMInfo vms) {
		this.vms = vms;
		this.resultMapModel = new STAFMapClassDefinition("STAF/Service/JMX/ListMBeanObjects");
		this.resultMapModel.addKey(Constants.VMID, Constants.T_VIRTUAL_MACHINE_ID);
		this.resultMapModel.addKey(Constants.DISPLAY_NAME, Constants.T_DISPLAY_NAME);
		this.resultMapModel.addKey(Constants.OBJECTS, Constants.T_MBEAN_OBJECTS);
	}

	/** {@inheritDoc} */
	@Override
	public STAFResult execute(final STAFCommandParseResult parseResult) {
		STAFResult result = new STAFResult(STAFResult.Ok);
		// Get the VM for the ID ...
		String requestedVMID = parseResult.optionValue(Constants.VMID, 1);
		VirtualMachineDescriptor vmd = vms.getVmd(requestedVMID);
		MBeanServerConnector connector = new MBeanServerConnector(vmd);

		try {
			LOG.info("Looking for Objects");
			MBeanServerConnection mbc = connector.connect();
			Set<ObjectName> objects = mbc.queryNames(null, null);
			result = createMachineResult(vmd, objects);

		} catch (MBeanServerConnectionException mbscx) {
			result = buildFailureStatus(mbscx.getCode(), vmd);
			LOG.severe(result.result + "\n" + ExceptionToStacktraceString.toStacktrace(mbscx));

		} catch (Exception iox) {
			result = buildFailureStatus(ReturnCode.ServerCommunicationError, vmd);
			LOG.severe(result.result + ": "	+ ExceptionToStacktraceString.toStacktrace(iox));


		} finally {
			connector.disconnect();
		}

		return result;
	}

	private STAFResult buildFailureStatus(ReturnCode rc, VirtualMachineDescriptor vmd) {
		String info = vmd.id() + " (" + vmd.displayName() + ")";
		return buildFailureStatus(rc, info);
	}

	private STAFResult buildFailureStatus(ReturnCode rc, String additionalInfo) {
		STAFResult result = new STAFResult(rc.getRC());
		result.result = rc.getMessage() + " " + additionalInfo;
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private STAFResult createMachineResult(VirtualMachineDescriptor vmd, Set<ObjectName> objectNames) {
		STAFMarshallingContext mc = new STAFMarshallingContext();
		mc.setMapClassDefinition(resultMapModel);
		Map resultMap = resultMapModel.createInstance();
		resultMap.put(Constants.VMID, vmd.id());
		resultMap.put(Constants.DISPLAY_NAME, vmd.displayName());

		List<String> objects = new ArrayList<String>();
		for (ObjectName on : objectNames) {
			objects.add(on.getCanonicalName());
		}
		resultMap.put(Constants.OBJECTS, objects);
		mc.setRootObject(resultMap);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}

	/** {@inheritDoc} */
	@Override
	public String getCommandHelp() {
		return Constants.OBJECTS + " " + Constants.VMID + " <" + Constants.T_VIRTUAL_MACHINE_ID + "> ";
	}
}
