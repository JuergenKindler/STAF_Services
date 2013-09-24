package org.jki.staf.service.jmx.commands.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.OpenType;

import org.jki.staf.service.ReturnCode;
import org.jki.staf.service.commands.CommandAction;
import org.jki.staf.service.jmx.commands.Constants;
import org.jki.staf.service.jmx.commands.QueryCommand;
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
 * The Class QueryAttributeAction allows to query attribute contents from
 * management beans
 */
public class QueryAttributesAction implements CommandAction {
	private static final Logger LOG = Logger.getLogger(QueryCommand.class.getSimpleName());
	private VMInfo vms;
	private List<String> simpleTypes;
	private STAFMapClassDefinition resultMapModel;
	private STAFMapClassDefinition mBeanModel;
	private STAFMapClassDefinition mBeanAttributeModel;

	/**
	 * Instantiates a new query attribute action.
	 * 
	 * @param vms
	 *            - the vms
	 */
	public QueryAttributesAction(VMInfo vms) {
		this.vms = vms;
		this.resultMapModel = new STAFMapClassDefinition("STAF/Service/JMX/QueryMBeanAttributes");
		this.resultMapModel.addKey(Constants.VMID, Constants.T_VIRTUAL_MACHINE_ID);
		this.resultMapModel.addKey(Constants.DISPLAY_NAME, Constants.T_DISPLAY_NAME);
		this.resultMapModel.addKey(Constants.OBJECTS, Constants.T_MBEAN_OBJECTS);

		this.mBeanModel = new STAFMapClassDefinition("STAF/Service/JMX/MBeanEntry");
		this.mBeanModel.addKey(Constants.OBJECT, Constants.T_MBEAN_OBJECT);
		this.mBeanModel.addKey(Constants.ATTRIBUTES, Constants.T_MBEAN_ATTRIBUTES);

		this.mBeanAttributeModel = new STAFMapClassDefinition("STAF/Service/JMX/MBeanAttributeEntry");
		this.mBeanAttributeModel.addKey(Constants.ATTRIBUTE_NAME, Constants.T_MBEAN_ATTRIBUTE_NAME);
		this.mBeanAttributeModel.addKey(Constants.ATTRIBUTE_VALUE, Constants.T_MBEAN_ATTRIBUTE_VALUE);
		
		this.simpleTypes = new ArrayList<String>();
		for (String type : OpenType.ALLOWED_CLASSNAMES_LIST) {
			if (!(type.startsWith("javax."))) {
				this.simpleTypes.add(type);
			}
		}
	}

	/**
	 * Select object names that match the expected pattern
	 * 
	 * @param mbc
	 *            - an MBean server connection
	 * @param oNamePattern
	 *            - the pattern for object name selection
	 * @return a set of ObjectNames
	 * @throws IOException
	 *             - when querying fails.
	 */
	private Set<ObjectName> selectObjectNames(MBeanServerConnection mbc, String oNamePattern) throws IOException {
		Set<ObjectName> objects = new TreeSet<ObjectName>();
		for (ObjectName oName : mbc.queryNames(null, null)) {
			if (oName.toString().matches(oNamePattern)) {
				LOG.info("Selected object name " + oName.toString());
				objects.add(oName);
			}
		}
		return objects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jki.staf.service.commands.CommandAction#execute(com.ibm.staf.service
	 * .STAFCommandParseResult)
	 */
	@Override
	public STAFResult execute(STAFCommandParseResult parseResult) {
		STAFResult result = new STAFResult(STAFResult.Ok);
		// Get the VM for the ID ...
		String requestedVMID = parseResult.optionValue(Constants.VMID, 1);
		VirtualMachineDescriptor vmd = vms.getVmd(requestedVMID);
		MBeanServerConnector connector = new MBeanServerConnector(vmd);

		try {
			LOG.info("Looking for Objects");
			MBeanServerConnection mbc = connector.connect();
			Set<ObjectName> objects = selectObjectNames(mbc, parseResult.optionValue(Constants.OBJECT));
			result = createMBeanObjectResult(vmd, mbc, objects, parseResult.optionValue(Constants.ATTRIBUTE));

		} catch (MBeanServerConnectionException mbscx) {
			result = buildFailureStatus(mbscx.getCode(), vmd);
			LOG.severe(result.result + "\n" + ExceptionToStacktraceString.toStacktrace(mbscx));

		} catch (Exception iox) {
			result = buildFailureStatus(ReturnCode.ServerCommunicationError, vmd);
			LOG.severe(result.result + ": " + ExceptionToStacktraceString.toStacktrace(iox));

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
	private STAFResult createMBeanObjectResult(VirtualMachineDescriptor vmd, MBeanServerConnection mbc,
			Set<ObjectName> objectNames, String attributePattern) throws InstanceNotFoundException,
			IntrospectionException, ReflectionException, IOException, MBeanException {
		STAFMarshallingContext mc = new STAFMarshallingContext();
		mc.setMapClassDefinition(resultMapModel);
		Map resultMap = resultMapModel.createInstance();
		resultMap.put(Constants.VMID, vmd.id());
		resultMap.put(Constants.DISPLAY_NAME, vmd.displayName());

		Map objectMap = mBeanModel.createInstance();
		for (ObjectName on : objectNames) {
			objectMap.put(Constants.OBJECT, on.getCanonicalName());
			objectMap.put(Constants.ATTRIBUTES, createMBeanAttributes(mbc, on, attributePattern));
		}
		resultMap.put(Constants.OBJECTS, objectMap);
		mc.setRootObject(resultMap);
		return new STAFResult(STAFResult.Ok, mc.marshall());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List createMBeanAttributes(MBeanServerConnection mbc, ObjectName mBeanName, String attributePattern)
			throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
		List attributes = new ArrayList();
		MBeanInfo mbi = mbc.getMBeanInfo(mBeanName);
		MBeanAttributeInfo[] attrInfo = mbi.getAttributes();

		for (MBeanAttributeInfo attr : attrInfo) {
			try {
				LOG.info(attr.getName() + "(" + attr.getType() + ")" + " matches " + attributePattern + " ?");
				if (isAttributeMatch(attr, attributePattern)) {
					LOG.info(attr.getName() + " matches " + attributePattern);
					Map attrMap = mBeanAttributeModel.createInstance();
					attrMap.put(Constants.ATTRIBUTE_NAME, attr.getName());
					attrMap.put(Constants.ATTRIBUTE_VALUE, mbc.getAttribute(mBeanName, attr.getName()).toString());
					attributes.add(attrMap);
				}
			} catch (MBeanException mbe) {
				LOG.severe("Could not access MBean instance " + mBeanName.toString() + ": "
						+ ExceptionToStacktraceString.toStacktrace(mbe));
			} catch (InstanceNotFoundException inf) {
				LOG.severe("Could not access MBean instance " + mBeanName.toString() + ": "
						+ ExceptionToStacktraceString.toStacktrace(inf));
			} catch (AttributeNotFoundException anf) {
				LOG.severe("Could not access attribute " + attr.getName() + " on MBean " + mBeanName.toString() + ": "
						+ ExceptionToStacktraceString.toStacktrace(anf));
			}
		}

		return attributes;
	}

	private boolean isAttributeMatch(MBeanAttributeInfo attr, String attributePattern) {
		boolean isMatched = attr.isReadable() && attr.getName().matches(attributePattern);
		return isMatched && simpleTypes.contains(attr.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jki.staf.service.commands.CommandAction#getCommandHelp()
	 */
	@Override
	public String getCommandHelp() {
		return Constants.ATTRIBUTES + " " + Constants.VMID + " <" + Constants.T_VIRTUAL_MACHINE_ID + "> "
				+ Constants.OBJECT + " <" + Constants.T_MBEAN_OBJECT + " pattern> " + Constants.ATTRIBUTE + " <"
				+ Constants.T_MBEAN_ATTRIBUTE + " pattern>";
	}
}
