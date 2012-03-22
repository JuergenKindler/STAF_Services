/**
 * 
 */
package org.jki.staf.service.jmx.vmtools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sun.tools.attach.AttachNotSupportedException;
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

	/**
	 * Construct a new VMInfo instance
	 */
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

	/**
	 * @return all local VM descriptors the current user can see.
	 */
	public final List<VirtualMachineDescriptor> getVMDescriptors() {
		return VirtualMachine.list();
	}

	/**
	 * @return a list of all VM identifiers.
	 */
	public final List<String> getVmIds() {
		return getListOf(VMField.ID);
	}

	/**
	 * @return a list of all VM display names.
	 */
	public final List<String> getVmDisplayNames() {
		return getListOf(VMField.DisplayName);
	}

	/**
	 * @param id
	 *            - the vm descriptor id of the vm to be found.
	 * @return the vm descriptor with the specified id or null, if none was
	 *         found.
	 */
	public final VirtualMachineDescriptor getVmd(String id) {
		List<VirtualMachineDescriptor> vmList = getVMDescriptors();

		for (Iterator<VirtualMachineDescriptor> vmdi = vmList.iterator(); vmdi.hasNext();) {
			VirtualMachineDescriptor vmd = vmdi.next();

			if (id.equals(vmd.id())) {
				return vmd;
			}
		}
		return null;
	}

	/**
	 * Get the virtual machine for a specific VM id.
	 * 
	 * @param id
	 *            - the virtual machine id
	 * @return the instance representing the virtual machine with the specified
	 *         id or none if the id was not found
	 * @throws AttachNotSupportedException
	 *             - if the virtual machine does not allow attaching to it
	 * @throws IOException
	 *             - in case of communication problems
	 */
	public final VirtualMachine getVirtualMachine(String id) throws AttachNotSupportedException, IOException {
		VirtualMachineDescriptor vmd = getVmd(id);
		return (vmd != null) ? VirtualMachine.attach(vmd) : null;
	}

}
