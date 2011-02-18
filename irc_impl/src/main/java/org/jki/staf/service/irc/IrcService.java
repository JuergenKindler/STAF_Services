/**
 * 
 */
package org.jki.staf.service.irc;

import java.util.HashMap;
import java.util.Map;

import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.irc.commands.HelpCommand;
import org.jki.staf.service.irc.commands.VersionCommand;
import org.jki.staf.service.util.ActionExtractor;
import org.jki.staf.service.util.DefaultErrorHandler;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFServiceInterfaceLevel30;

/**
 * A service that allows to configure irc servers and send messages to them.
 */
public class IrcService implements STAFServiceInterfaceLevel30 {
	private static String CR = System.getProperty("line.separator");
	private static String CMD_VERSION = "VERSION";
	private static String CMD_HELP = "HELP";
	private static String SERVICE_NAME = IrcService.class.getSimpleName();

	private static String HELP_MSG = "*** " + SERVICE_NAME
			+ " Service Help ***" + CR + CR + CMD_VERSION + CR + CMD_HELP + CR;

	private InitInfo initInfo;
	private STAFHandle handle;
	private DefaultErrorHandler errorHandler;
	private ActionExtractor extractor;
	private Map<String, ServiceCommand> commands;
	private String localMachineName;

	/**
	 * Default constructor.
	 */
	public IrcService() {
		super();
		errorHandler = new DefaultErrorHandler(SERVICE_NAME);
		extractor = new ActionExtractor();
		commands = new HashMap<String, ServiceCommand>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.staf.service.STAFServiceInterfaceLevel30#acceptRequest(com.ibm
	 * .staf.service.STAFServiceInterfaceLevel30.RequestInfo)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.staf.service.STAFServiceInterfaceLevel30#init(com.ibm.staf.service
	 * .STAFServiceInterfaceLevel30.InitInfo)
	 */
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
		commands.put(CMD_HELP, new HelpCommand(CMD_HELP, localMachineName,
				initInfo, HELP_MSG));
		commands.put(CMD_VERSION, new VersionCommand(CMD_VERSION, localMachineName,
				initInfo));
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.staf.service.STAFServiceInterfaceLevel30#term()
	 */
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
