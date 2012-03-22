/**
 * 
 */
package org.jki.staf.service.jmx.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jki.staf.service.commands.AbstractServiceCommand;
import org.jki.staf.service.commands.CommandAction;
import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.jmx.commands.list.ListAttributesAction;
import org.jki.staf.service.jmx.commands.list.ListObjectsAction;
import org.jki.staf.service.jmx.commands.list.ListServersAction;
import org.jki.staf.service.jmx.vmtools.VMInfo;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * @author jkindler
 * 
 */
public class ListCommand extends AbstractServiceCommand implements ServiceCommand {
	public static final String ATTRIBUTES = "ATTRIBUTES";
	public static final String DISPLAY_NAME = "DISPLAY_NAME";
	public static final String OBJECT = "OBJECT";
	public static final String OBJECTS = OBJECT + "S";
	public static final String VMID = "VMID";
	public static final String VMIDS = VMID + "S";
	private static final Logger LOG = Logger.getLogger(ListCommand.class.getSimpleName());

	private static final String CR = System.getProperty("line.separator");
	private Map<String, CommandAction> subCommands;
	private String help;

	/**
	 * Create a wrapper for all variations of listing items
	 * 
	 * @param name - the command name
	 * @param machineName - the command name in staf
	 * @param initInfo - staf internal initialization info
	 * @param vms - the virtual machine info
	 */
	public ListCommand(String name, String machineName, InitInfo initInfo, VMInfo vms) {
		super(name, machineName, initInfo);
		
		// Add the different actions / command variations
		subCommands = new HashMap<String, CommandAction>();
		subCommands.put(VMIDS, new ListServersAction(vms));
		subCommands.put(ATTRIBUTES, new ListAttributesAction(vms));
		subCommands.put(OBJECTS, new ListObjectsAction(vms));
	}

	/** {@inheritDoc} */
	@Override
	public STAFResult execute(RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);

		if (result.rc == STAFResult.Ok) {
			CommandAction command = null;
			String parsedCommandName = (parseResult.numInstances() >= 2) ? parseResult.instanceName(2).toUpperCase() : null;
			LOG.info("Looking for command action with name " + parsedCommandName);
			
			if (parsedCommandName != null) {
				command = subCommands.get(parsedCommandName);
			} else {
				LOG.warning("No command specified");
			}

			if (command != null) {
				result = command.execute(parseResult);
			} else {
				result = new STAFResult(STAFResult.InvalidParm, "No service command found.");
			}
		}
		return result;
	}
	
	/** {@inheritDoc} */
	@Override
	public String getCommandHelp() {
		if (help == null) {
			StringBuilder sb = new StringBuilder();
			for (CommandAction sc : subCommands.values()) {
				sb.append(getCommandName()).append(" ").append(sc.getCommandHelp()).append(CR);
			}
			help = sb.toString();
		}
		return help;
	}


	/** {@inheritDoc} */
	protected void setupParser() {
		/*
		 * LIST VMIDS [DISPLAY_NAME]
		 * LIST OBJECTS VMID <id>
		 * LIST ATTRIBUTES VMID <id> OBJECT <objectName>
		 */
		super.setupParser();
		parser.addOption(VMIDS, 0, STAFCommandParser.VALUENOTALLOWED);
		parser.addOption(OBJECTS, 0, STAFCommandParser.VALUENOTALLOWED);
		parser.addOption(ATTRIBUTES, 0, STAFCommandParser.VALUENOTALLOWED);
		parser.addOptionGroup(getList(VMIDS, OBJECTS, ATTRIBUTES), 1, 1);
		
		parser.addOption(DISPLAY_NAME, 0, STAFCommandParser.VALUENOTALLOWED);
//		parser.addOptionNeed(DISPLAY_NAME, VMIDS);
		
		parser.addOption(VMID, 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOptionNeed(VMID, getList(OBJECTS, ATTRIBUTES));

		parser.addOption(OBJECT, 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOptionNeed(ATTRIBUTES, OBJECT);
	}


	/** {@inheritDoc} */
	@Override
	protected int getTrustLevel() {
		return 4;
	}
}
