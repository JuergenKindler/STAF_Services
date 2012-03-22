package org.jki.staf.service.jmx.commands.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jki.staf.service.ReturnCode;
import org.jki.staf.service.commands.CommandAction;
import org.jki.staf.service.jmx.commands.ListCommand;
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
public class ListAttributesAction implements CommandAction {
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
	public ListAttributesAction(VMInfo vms) {
		this.vms = vms;
		this.resultMapModel = new STAFMapClassDefinition("STAF/Service/JMX/ListMBeanAttributes");
		this.resultMapModel.addKey(ListCommand.VMID, "VM identifier");
		this.resultMapModel.addKey(ListCommand.DISPLAY_NAME, "VM display name");
		this.resultMapModel.addKey(ListCommand.OBJECT, "MBean Object");
		this.resultMapModel.addKey(ListCommand.ATTRIBUTES, "MBean Attributes");
	}

	/** {@inheritDoc} */
	@Override
	public STAFResult execute(final STAFCommandParseResult parseResult) {
		STAFResult result = new STAFResult(STAFResult.Ok);
		// Get the VM for the ID ...
		String requestedVMID = parseResult.optionValue(ListCommand.VMID, 1);
		VirtualMachineDescriptor vmd = vms.getVmd(requestedVMID);
		MBeanServerConnector connector = new MBeanServerConnector(vmd);

		// Verify the object ID and attempt to get the MBean instance
		String objectNameString = parseResult.optionValue(ListCommand.OBJECT, 1);

		try {
			LOG.info("Looking for MBean '" + objectNameString + "'");
			ObjectName beanObjectName = new ObjectName(objectNameString);
			MBeanServerConnection mbc = connector.connect();
			MBeanInfo mbi = mbc.getMBeanInfo(beanObjectName);
			result = createMachineResult(vmd, beanObjectName, mbi.getAttributes());

		} catch (MBeanServerConnectionException mbscx) {
			result = buildFailureStatus(mbscx.getCode(), vmd);
			LOG.severe(result.result + "\n" + ExceptionToStacktraceString.toStacktrace(mbscx));

		} catch (MalformedObjectNameException e) {
			String info = ListCommand.OBJECT + " = '" + objectNameString + "' : "
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
	private STAFResult createMachineResult(VirtualMachineDescriptor vmd, ObjectName object, MBeanAttributeInfo[] attr) {
		STAFMarshallingContext mc = new STAFMarshallingContext();
		mc.setMapClassDefinition(resultMapModel);
		Map resultMap = resultMapModel.createInstance();
		resultMap.put(ListCommand.VMID, vmd.id());
		resultMap.put(ListCommand.DISPLAY_NAME, vmd.displayName());
		resultMap.put(ListCommand.OBJECT, object.getCanonicalName());

		List<String> attributes = new ArrayList<String>();
		for (MBeanAttributeInfo ai : attr) {
			attributes.add(ai.getName());
		}
		resultMap.put(ListCommand.ATTRIBUTES, attributes);
		mc.setRootObject(resultMap);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}

	/** {@inheritDoc} */
	@Override
	public String getCommandHelp() {
		return ListCommand.ATTRIBUTES + " " + ListCommand.VMID + " <VirtualMachineID> " + ListCommand.OBJECT
				+ " <MBean Object Name>";
	}
}
