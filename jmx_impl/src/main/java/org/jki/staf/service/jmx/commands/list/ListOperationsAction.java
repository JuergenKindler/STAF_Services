package org.jki.staf.service.jmx.commands.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
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
public class ListOperationsAction implements CommandAction {
	private static final Logger LOG = Logger.getLogger(ListCommand.class.getSimpleName());
	private VMInfo vms;
	private STAFMapClassDefinition resultMapModel;

	/**
	 * Create a command to list all attribute names of an MBean
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
	public ListOperationsAction(VMInfo vms) {
		this.vms = vms;
		this.resultMapModel = new STAFMapClassDefinition("STAF/Service/JMX/ListMBeanOperations");
		this.resultMapModel.addKey(Constants.VMID, Constants.T_VIRTUAL_MACHINE_ID);
		this.resultMapModel.addKey(Constants.DISPLAY_NAME, Constants.T_DISPLAY_NAME);
		this.resultMapModel.addKey(Constants.OBJECT, Constants.T_MBEAN_OBJECT);
		this.resultMapModel.addKey(Constants.OPERATIONS, Constants.T_MBEAN_OPERATIONS);
	}

	/** {@inheritDoc} */
	@Override
	public STAFResult execute(final STAFCommandParseResult parseResult) {
		STAFResult result = new STAFResult(STAFResult.Ok);
		// Get the VM for the ID ...
		String requestedVMID = parseResult.optionValue(Constants.VMID, 1);
		VirtualMachineDescriptor vmd = vms.getVmd(requestedVMID);
		MBeanServerConnector connector = new MBeanServerConnector(vmd);

		// Verify the object ID and attempt to get the MBean instance
		String objectNameString = parseResult.optionValue(Constants.OBJECT, 1);

		try {
			LOG.info("Looking for operations of MBean '" + objectNameString + "'");
			ObjectName beanObjectName = new ObjectName(objectNameString);
			MBeanServerConnection mbc = connector.connect();
			MBeanInfo mbi = mbc.getMBeanInfo(beanObjectName);
			result = createMachineResult(vmd, beanObjectName, mbi.getOperations());

		} catch (MBeanServerConnectionException mbscx) {
			result = buildFailureStatus(mbscx.getCode(), vmd);
			LOG.severe(result.result + "\n" + ExceptionToStacktraceString.toStacktrace(mbscx));

		} catch (MalformedObjectNameException e) {
			String info = Constants.OBJECT + " = '" + objectNameString + "' : "
					+ ExceptionToStacktraceString.toStacktrace(e);
			result = buildFailureStatus(ReturnCode.MBeanObjectIDInvalid, info);
			LOG.severe(result.result);

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
	private STAFResult createMachineResult(VirtualMachineDescriptor vmd, ObjectName object, MBeanOperationInfo[] ops) {
		STAFMarshallingContext mc = new STAFMarshallingContext();
		mc.setMapClassDefinition(resultMapModel);
		Map resultMap = resultMapModel.createInstance();
		resultMap.put(Constants.VMID, vmd.id());
		resultMap.put(Constants.DISPLAY_NAME, vmd.displayName());
		resultMap.put(Constants.OBJECT, object.getCanonicalName());

		List<String> operations = new ArrayList<String>();
		for (MBeanOperationInfo op : ops) {
			LOG.info("Operation: " + op.toString());
			operations.add(getOperationSignature(op));
		}
		resultMap.put(Constants.OPERATIONS, operations);
		mc.setRootObject(resultMap);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}
	
	private String getOperationSignature(MBeanOperationInfo oi) {
		String comma = "";
		StringBuilder sb = new StringBuilder(oi.getReturnType());
		sb.append(" ").append(oi.getName()).append("(");

		MBeanParameterInfo[] pil = oi.getSignature();
		for (MBeanParameterInfo pi : pil) {
			sb.append(comma).append(pi.getType()).append(" ").append(pi.getName());
			comma = ",";
		}
		sb.append(")");
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getCommandHelp() {
		return Constants.OPERATIONS + " " + Constants.VMID + " <" + Constants.T_VIRTUAL_MACHINE_ID + "> " + Constants.OBJECT
				+ " <" + Constants.T_MBEAN_OBJECT + ">";
	}
}
