package org.jki.staf.service.jmx.commands.list;

import java.util.List;

import org.jki.staf.service.commands.CommandAction;
import org.jki.staf.service.jmx.commands.Constants;
import org.jki.staf.service.jmx.vmtools.VMInfo;

import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.ibm.staf.service.STAFCommandParseResult;

/**
 * @author jkindler
 * 
 */
public class ListServersAction implements CommandAction {
	private VMInfo vms;

	/**
	 * Create a command to list all local jmx servers
	 * @param initInfo
	 *            - staf internal initialization info
	 * @param vms
	 *            - the virtual machine info
	 */
	public ListServersAction(VMInfo vms) {
		this.vms = vms;
	}

	/** {@inheritDoc} */
	@Override
	public STAFResult execute(final STAFCommandParseResult parseResult) {
		STAFResult result = new STAFResult(STAFResult.Ok);

		if (result.rc == STAFResult.Ok) {
			List<String> l = (parseResult.optionTimes(Constants.DISPLAY_NAME) > 0) ? vms.getVmDisplayNames() : vms.getVmIds();
			String marshalledList = STAFMarshallingContext.marshall(l, null);
			result.result = marshalledList;
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	public String getCommandHelp() {
		return Constants.VMIDS + " [" + Constants.DISPLAY_NAME + "]";
	}
}
