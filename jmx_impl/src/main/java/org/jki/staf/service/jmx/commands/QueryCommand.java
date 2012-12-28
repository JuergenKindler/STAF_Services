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
import org.jki.staf.service.jmx.commands.query.QueryAttributesAction;
import org.jki.staf.service.jmx.commands.query.QueryOperationsAction;
import org.jki.staf.service.jmx.vmtools.VMInfo;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * @author jkindler
 */
public class QueryCommand extends AbstractServiceCommand implements ServiceCommand, Constants {
	private static final Logger LOG = Logger.getLogger(QueryCommand.class.getSimpleName());

	private static final String CR = System.getProperty("line.separator");
	private Map<String, CommandAction> subCommands;
	private String help;

	/**
	 * Create a wrapper for all variations of query items.
	 * 
	 * @param name - the command name
	 * @param machineName - the command name in staf
	 * @param initInfo - staf internal initialization info
	 * @param vms - the virtual machine info
	 */
	public QueryCommand(String name, String machineName, InitInfo initInfo, VMInfo vms) {
		super(name, machineName, initInfo);
		
		// Add the different actions / command variations
		subCommands = new HashMap<String, CommandAction>();
		subCommands.put(ATTRIBUTE, new QueryAttributesAction(vms));
		subCommands.put(OPERATION, new QueryOperationsAction(vms));
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
		 * QUERY VMID <id>
		 * QUERY VMID <id> OBJECT <objectPattern>
		 * QUERY VMID <id> OBJECT <objectPattern> ATTRIBUTE <attributePattern>
		 */
		super.setupParser();
		parser.addOption(VMIDS, 0, STAFCommandParser.VALUENOTALLOWED);
		parser.addOption(OBJECTS, 0, STAFCommandParser.VALUENOTALLOWED);
		parser.addOption(ATTRIBUTES, 0, STAFCommandParser.VALUENOTALLOWED);
		parser.addOptionGroup(getList(VMIDS, OBJECTS, ATTRIBUTES), 1, 1);
		
		parser.addOption(DISPLAY_NAME, 0, STAFCommandParser.VALUENOTALLOWED);
		
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
