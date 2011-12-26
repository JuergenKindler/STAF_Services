package org.jki.staf.service;

import java.util.HashMap;
import java.util.Map;

import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.util.ActionExtractor;
import org.jki.staf.service.util.DefaultErrorHandler;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFServiceInterfaceLevel30;

/**
 * A generic staf service class containing commands and basic handlers.
 * @author ngjo
 */
public abstract class GenericStafService  implements STAFServiceInterfaceLevel30 {
	protected static String CR = System.getProperty("line.separator");
	protected String serviceName = getClass().getSimpleName();
	protected String helpMessage = "*** " + serviceName
				+ " Service Help ***" + CR + CR + ServiceCommand.VERSION + CR + ServiceCommand.HELP + CR;
	protected InitInfo initInfo;
	protected STAFHandle handle;
	protected DefaultErrorHandler errorHandler;
	protected ActionExtractor extractor;
	protected Map<String, ServiceCommand> commands;
	protected String localMachineName;

	public GenericStafService() {
		super();
		errorHandler = new DefaultErrorHandler(serviceName);
		extractor = new ActionExtractor();
		commands = new HashMap<String, ServiceCommand>();
	}

	/** {@inheritDoc}*/
	public STAFResult acceptRequest(RequestInfo reqInfo) {
		STAFResult result = null;
	
		try {
			// Determine the command request (the first word in the request)
			String action = extractor.getAction(reqInfo);
	
			// Call the appropriate command to handle the command request
			if (commands.containsKey(action.toUpperCase())) {
				result = commands.get(action).execute(reqInfo);
	
			} else {
				result = new STAFResult(STAFResult.InvalidRequestString, "'"
						+ action + "' is not a valid command request for the "
						+ serviceName + " service" + CR + CR + helpMessage);
			}
		} catch (Exception ex) {
			result = errorHandler.handleException(reqInfo, ex);
	
		} catch (Error err) {
			result = errorHandler.handleError(reqInfo, err);
		}
		return result;
	}

	/** {@inheritDoc}*/
	public STAFResult init(InitInfo initInformation) {
		STAFResult result = new STAFResult(STAFResult.Ok);
	
		initInfo = initInformation;
	
		try {
			handle = new STAFHandle(initInfo.name);
	
			// Resolve the machine name variable for the local machine
			STAFResult res = STAFUtil.resolveInitVar("{STAF/Config/Machine}", handle);
	
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
	 * Set up commands for this service.
	 */
	protected abstract void setupCommands();

	/** {@inheritDoc}*/
	public STAFResult term() {
		try {
			handle.unRegister();
		} catch (STAFException ex) {
			return errorHandler.handleException(initInfo, ex);
		}
	
		return new STAFResult(STAFResult.Ok);
	}

}