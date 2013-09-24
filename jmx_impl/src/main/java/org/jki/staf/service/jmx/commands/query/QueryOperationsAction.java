package org.jki.staf.service.jmx.commands.query;

import java.util.logging.Logger;

import org.jki.staf.service.commands.CommandAction;
import org.jki.staf.service.jmx.commands.Constants;
import org.jki.staf.service.jmx.commands.QueryCommand;
import org.jki.staf.service.jmx.vmtools.VMInfo;

import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;

/**
 * The Class QueryOperationAction.
 */
public class QueryOperationsAction implements CommandAction {
	private static final Logger LOG = Logger.getLogger(QueryCommand.class.getSimpleName());
	private VMInfo vms;

	/**
	 * Instantiates a new query operation action.
	 *
	 * @param vms the vms
	 */
	public QueryOperationsAction(VMInfo vms) {
		this.vms = vms;
	}

	/* (non-Javadoc)
	 * @see org.jki.staf.service.commands.CommandAction#execute(com.ibm.staf.service.STAFCommandParseResult)
	 */
	@Override
	public STAFResult execute(STAFCommandParseResult parseResult) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jki.staf.service.commands.CommandAction#getCommandHelp()
	 */
	@Override
	public String getCommandHelp() {
		return Constants.OPERATIONS + " " + Constants.VMID + " <" + Constants.T_VIRTUAL_MACHINE_ID + "> "
				+ Constants.OBJECT + " <" + Constants.T_MBEAN_OBJECT + " pattern> " + Constants.OPERATIONS + " <"
				+ Constants.T_MBEAN_OPERATION + " pattern>";
	}
}
