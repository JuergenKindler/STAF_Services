package org.jki.staf.service.jmx.commands.query;

import java.util.logging.Logger;

import org.jki.staf.service.commands.CommandAction;
import org.jki.staf.service.jmx.commands.Constants;
import org.jki.staf.service.jmx.commands.QueryCommand;
import org.jki.staf.service.jmx.vmtools.VMInfo;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;

/**
 * The Class QueryAttributeAction.
 */
public class QueryAttributesAction implements CommandAction {
	private static final Logger LOG = Logger.getLogger(QueryCommand.class.getSimpleName());
	private VMInfo vms;

	/**
	 * Instantiates a new query attribute action.
	 * 
	 * @param vms
	 *            the vms
	 */
	public QueryAttributesAction(VMInfo vms) {
		this.vms = vms;
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
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jki.staf.service.commands.CommandAction#getCommandHelp()
	 */
	@Override
	public String getCommandHelp() {
		return Constants.ATTRIBUTES + " " + Constants.VMID + " <" + Constants.T_VIRTUAL_MACHINE_ID + "> "
				+ Constants.OBJECT + " <" + Constants.T_MBEAN_OBJECT + "> " + Constants.ATTRIBUTES + " <"
				+ Constants.T_MBEAN_ATTRIBUTE + "[," + Constants.T_MBEAN_ATTRIBUTE + "]>";
	}
}
