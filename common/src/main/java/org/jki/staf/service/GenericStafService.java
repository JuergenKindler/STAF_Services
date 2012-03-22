package org.jki.staf.service;

import java.util.Map;
import java.util.TreeMap;

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
 * 
 * @author ngjo
 */
public abstract class GenericStafService implements STAFServiceInterfaceLevel30 {
	/**
	 * A short cut for a line separator
	 */
	protected final static String CR = System.getProperty("line.separator");

	/**
	 * The service name is the simple name of the class
	 */
	protected String serviceName = getClass().getSimpleName();

	/**
	 * The default help message
	 */
	protected String helpMessage = "*** " + serviceName + " Service Help ***"
			+ CR + ServiceCommand.VERSION + CR + ServiceCommand.HELP + CR;

	/**
	 * The staf internal init info
	 */
	protected InitInfo initInfo;

	/**
	 * The service's staf handle
	 */
	protected STAFHandle handle;

	/**
	 * The handler to cover exceptions
	 */
	protected DefaultErrorHandler errorHandler;

	/**
	 * The helper that extracts an action from a STAF request
	 */
	protected ActionExtractor extractor;

	/**
	 * The map of all service commands.
	 */
	protected Map<String, ServiceCommand> commands;

	/**
	 * The name of the local machine.
	 */
	protected String localMachineName;

	/**
	 * The list of all known return codes.
	 */
	protected Map<Integer,ReturnCode> codes;

	/**
	 * Create a generic service
	 */
	public GenericStafService() {
		super();
		errorHandler = new DefaultErrorHandler(serviceName);
		extractor = new ActionExtractor();
		commands = new TreeMap<String, ServiceCommand>();
		codes = new TreeMap<Integer, ReturnCode>();
	}

	/** {@inheritDoc} */
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
						+ serviceName + " service" + CR + helpMessage);
			}
		} catch (Exception ex) {
			result = errorHandler.handleException(reqInfo, ex);

		} catch (Error err) {
			result = errorHandler.handleError(reqInfo, err);
		}
		return result;
	}

	/** {@inheritDoc} */
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
				
				for (ReturnCode code : codes.values()) {
					registerCode(code);
				}
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

	/**
	 * Add one or more codes to the internal map of codes.
	 * @param code - one or more return codes.
	 */
	protected final void addCode(ReturnCode... code) {
		for (ReturnCode co : code) {
			codes.put(new Integer(co.getRC()), co);
		}
	}

	/**
	 * Register error number in help system.
	 * @param code - the code to register
	 * @return the result of registering a new return code.
	 */
	protected STAFResult registerCode(ReturnCode code) {
		return handle.submit2("local", "HELP",
				"REGISTER SERVICE " + serviceName + " ERROR " + code.getRC()
						+ " INFO " + STAFUtil.wrapData(code.getMessage() + " (" + code.name() + ")") + " DESCRIPTION "
						+ STAFUtil.wrapData(code.getMessage()));
	}

	/**
	 * Unregister error number.
	 * @param code - the error code to unregister
	 * @return the result of the unregister operation
	 */
	protected STAFResult unregisterCode(ReturnCode code) {
		return handle.submit2("local", "HELP", "UNREGISTER SERVICE "
				+ serviceName + " ERROR " + code.getRC());
	}

	/** {@inheritDoc} */
	public STAFResult term() {
		try {
			for (ReturnCode code : codes.values()) {
				unregisterCode(code);
			}

			handle.unRegister();
		} catch (STAFException ex) {
			return errorHandler.handleException(initInfo, ex);
		}

		return new STAFResult(STAFResult.Ok);
	}

}