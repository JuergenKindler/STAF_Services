/**
 * 
 */
package org.jki.staf.service.jmx.vmtools;

import java.util.ArrayList;
import java.util.List;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * Allows access to all existing VMs
 * 
 * @author ngjo
 */
public class VMInfo {
	private enum VMField {
		ID, DisplayName
	}

	public VMInfo() {
		super();
	}

	private final List<String> getListOf(VMField field) {
		List<String> fields = new ArrayList<String>();

		for (VirtualMachineDescriptor vm : this.getVMDescriptors()) {
			switch (field) {
			case ID:
				fields.add(vm.id());
				break;
			case DisplayName:
				fields.add(vm.displayName());
				break;
			}
		}
		return fields;
	}

	public final List<VirtualMachineDescriptor> getVMDescriptors() {
		return VirtualMachine.list();
	}

	public final List<String> getVmIds() {
		return getListOf(VMField.ID);
	}

	public final List<String> getVmDisplayNames() {
		return getListOf(VMField.DisplayName);
	}
}
