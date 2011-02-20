/**
 * 
 */
package org.jki.staf.service.irc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.irc.commands.CreateServerCommand;
import org.jki.staf.service.irc.commands.HelpCommand;
import org.jki.staf.service.irc.commands.VersionCommand;
import org.jki.staf.service.util.ActionExtractor;
import org.jki.staf.service.util.DefaultErrorHandler;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFServiceInterfaceLevel30;

import org.schwering.irc.lib.IRCConnection;

/**
 * A service that allows to configure irc servers and send messages to them.
 */
public class IrcService implements STAFServiceInterfaceLevel30, ConnectionHolder {
	private static String CR = System.getProperty("line.separator");
	
	private static String SERVICE_NAME = IrcService.class.getSimpleName();

	private static String HELP_MSG = "*** " + SERVICE_NAME
			+ " Service Help ***" + CR + CR + ServiceCommand.VERSION + CR + ServiceCommand.HELP + CR;

	private InitInfo initInfo;
	private STAFHandle handle;
	private DefaultErrorHandler errorHandler;
	private ActionExtractor extractor;
	private Map<String, ServiceCommand> commands;
	private String localMachineName;
	private List<IRCConnection> connections;

	
	/**
	 * Default constructor.
	 */
	public IrcService() {
		super();
		errorHandler = new DefaultErrorHandler(SERVICE_NAME);
		extractor = new ActionExtractor();
		commands = new HashMap<String, ServiceCommand>();
		connections = new ArrayList<IRCConnection>();
	}

	
    /** {@inheritDoc}*/
	@Override
	public STAFResult acceptRequest(RequestInfo reqInfo) {
		STAFResult result = null;

		try {
			// Determine the command request (the first word in the request)
			String action = extractor.getAction(reqInfo);
			System.out.println(); // 

			// Call the appropriate command to handle the command request
			if (commands.containsKey(action.toUpperCase())) {
				result = commands.get(action).execute(reqInfo);

			} else {
				result = new STAFResult(STAFResult.InvalidRequestString, "'"
						+ action + "' is not a valid command request for the "
						+ SERVICE_NAME + " service" + CR + CR + HELP_MSG);
			}
		} catch (Exception ex) {
			result = errorHandler.handleException(reqInfo, ex);

		} catch (Error err) {
			result = errorHandler.handleError(reqInfo, err);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see org.jki.staf.service.irc.ConnectionHolder#add(org.schwering.irc.lib.IRCConnection)
	 */
	@Override
	public void add(IRCConnection connection) {
		connections.add(connection);
	}


	/* (non-Javadoc)
	 * @see org.jki.staf.service.irc.ConnectionHolder#remove(org.schwering.irc.lib.IRCConnection)
	 */
	@Override
	public boolean remove(IRCConnection connection) {
		return connections.remove(connection);
	}


	/* (non-Javadoc)
	 * @see org.jki.staf.service.irc.ConnectionHolder#getConnections()
	 */
	@Override
	public List<IRCConnection> getConnections() {
		return Collections.unmodifiableList(new ArrayList<IRCConnection>(connections));
	}
	
	
	/** {@inheritDoc}*/
	@Override
	public STAFResult init(InitInfo initInformation) {
		STAFResult result = new STAFResult(STAFResult.Ok);

		initInfo = initInformation;

		try {
			handle = new STAFHandle(initInfo.name);

			// Resolve the machine name variable for the local machine
			STAFResult res = STAFUtil.resolveInitVar("{STAF/Config/Machine}",
					handle);

			if (res.rc != STAFResult.Ok) {
				result = res;
			} else {
				localMachineName = res.result;
				setupCommands();
			}

		} catch (STAFException e) {
			result = errorHandler.handleException(initInfo, e);
		}

		return result;
	}

	/**
	 * Create and register all commands
	 */
	private void setupCommands() {
		commands.put(ServiceCommand.HELP
				, new HelpCommand(ServiceCommand.HELP, localMachineName, initInfo, HELP_MSG));
		commands.put(ServiceCommand.VERSION
				, new VersionCommand(ServiceCommand.VERSION, localMachineName, initInfo));
		commands.put(ServiceCommand.CREATE
				, new CreateServerCommand(ServiceCommand.CREATE, localMachineName, initInfo, this));
	}

	
    /** {@inheritDoc}*/
	@Override
	public STAFResult term() {
		try {
			handle.unRegister();
		} catch (STAFException ex) {
			return errorHandler.handleException(initInfo, ex);
		}

		return new STAFResult(STAFResult.Ok);
	}
}
