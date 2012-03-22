package org.jki.staf.service.jmx.vmtools;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.jki.staf.service.ReturnCode;
import org.jki.staf.service.util.ExceptionToStacktraceString;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/**
 * @author jkindler
 */
public class MBeanServerConnector {
	private static final Logger LOG = Logger.getLogger(MBeanServerConnector.class.getSimpleName());
	private static final String LOCAL_CONNECTOR = "com.sun.management.jmxremote.localConnectorAddress";
	private VirtualMachineDescriptor vmd;
	private JMXConnector jmxCon = null;
	private MBeanServerConnection mbc = null;

	/**
	 * Create new connector.
	 * @param vmd - a virtual machine descriptor
	 */
	public MBeanServerConnector(VirtualMachineDescriptor vmd) {
		this.vmd = vmd;
	}

	/**
	 * Connect to MBean server
	 * @return an MBeanServerConnection.
	 * @throws MBeanServerConnectionException - in case connection fails.
	 */
	public MBeanServerConnection connect() throws MBeanServerConnectionException {
		if (vmd == null) {
			throw new MBeanServerConnectionException(ReturnCode.ServerNotFound);
		}

		try {
			VirtualMachine vm = VirtualMachine.attach(vmd);

			String connectorAddress = getConnectorAddress(vm);
			if (connectorAddress == null) {
				throw new MBeanServerConnectionException(ReturnCode.ServerHasNoConnectorAddress);
			}

			JMXServiceURL url = new JMXServiceURL(connectorAddress);
			jmxCon = JMXConnectorFactory.connect(url);
			mbc = jmxCon.getMBeanServerConnection();

		} catch (AttachNotSupportedException ansx) {
			LOG.severe(ReturnCode.ServerAttachFailed.getMessage() + ": "
					+ ExceptionToStacktraceString.toStacktrace(ansx));
			throw new MBeanServerConnectionException(ReturnCode.ServerAttachFailed);

		} catch (Exception iox) {
			LOG.severe(ReturnCode.ServerCommunicationError.getMessage() + ": "
					+ ExceptionToStacktraceString.toStacktrace(iox));
			throw new MBeanServerConnectionException(iox, ReturnCode.ServerCommunicationError);
		}

		return mbc;
	}


	/**
	 * Disconnect jmx connection.
	 */
	public void disconnect() {
		if (jmxCon != null) {
			try {
				jmxCon.close();
			} catch (IOException e) {
				// ignore here >:->
			}
		}
	}


	private String getConnectorAddress(VirtualMachine vm) throws IOException, AgentLoadException,
			AgentInitializationException {
		Properties props = vm.getAgentProperties();
		String connectorAddress = props.getProperty(LOCAL_CONNECTOR);
		// no connector address, so we start the JMX agent
		if (connectorAddress == null) {
			String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator
					+ "management-agent.jar";
			vm.loadAgent(agent);

			// agent is started, get the connector address
			connectorAddress = vm.getAgentProperties().getProperty(LOCAL_CONNECTOR);
		}
		return connectorAddress;
	}
}
