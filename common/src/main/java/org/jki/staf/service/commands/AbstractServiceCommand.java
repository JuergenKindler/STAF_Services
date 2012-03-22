package org.jki.staf.service.commands;

import java.util.logging.Logger;

import org.jki.staf.service.commands.ServiceCommand;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

/**
 * An abstract service command that provides a basic implementation of a service
 * command.
 */
public abstract class AbstractServiceCommand implements ServiceCommand {
	protected static final Logger LOG = Logger.getLogger(AbstractServiceCommand.class.getSimpleName());

	/**
	 * The name of the command
	 */
	protected String name;

	/**
	 * The help of the command
	 */
	protected String help;

	/**
	 * The name of the local machine (where the service is installed)
	 */
	protected String localMachineName;

	/**
	 * Information from the initialization of the service
	 */
	protected InitInfo info;

	/**
	 * The command parser for this command. Necessary to parse all options from
	 * command line.
	 */
	protected STAFCommandParser parser;

	/**
	 * The result of the parameter parsing.
	 */
	protected STAFCommandParseResult parseResult;

	private AbstractServiceCommand() {
		super();
	}

	/**
	 * Create a new command.
	 * 
	 * @param commandName
	 *            - the name of the command.
	 * @param machineName
	 *            - the local machine name.
	 * @param initInfo
	 *            - the service initialization info.
	 */
	public AbstractServiceCommand(final String commandName, final String machineName, final InitInfo initInfo) {
		this();
		name = commandName;
		localMachineName = machineName;
		info = initInfo;
		setupParser();
	}

	/**
	 * Execute a request and return a STAF result. This implementation already
	 * parses the request and returns an error if the parse failed.
	 * 
	 * @param reqInfo
	 *            - the request
	 * @return a STAF Result instance
	 */
	@Override
	public STAFResult execute(final RequestInfo reqInfo) {
		STAFResult result = new STAFResult(STAFResult.Ok);

		STAFResult trustResult = STAFUtil.validateTrust(getTrustLevel(), info.name, name, localMachineName, reqInfo);

		if (trustResult.rc != STAFResult.Ok) {
			result = trustResult;

		} else {
			parseResult = parser.parse(reqInfo.request);

			if (parseResult.rc != STAFResult.Ok) {
				result = new STAFResult(STAFResult.InvalidRequestString, parseResult.errorBuffer);
			}
		}

		return result;
	}

	/**
	 * Write contents of parsing commands to log file
	 * 
	 * @param pr - parse result from STAF command line parser
	 */
	protected final void logParseResult(STAFCommandParseResult pr) {
		LOG.info("Arguments: " + pr.numArgs());
		LOG.info("Instances: " + pr.numInstances());
		LOG.info("To String: " + pr.toString());
		LOG.info("Error buffer: " + pr.errorBuffer);

		for (int i = 1; i <= pr.numInstances(); i++) {
			LOG.info("Argument (" + i + ") " + pr.instanceName(i) + " = " + pr.instanceValue(i));
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String getCommandName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public abstract String getCommandHelp();

	/**
	 * Returns the trust level required to execute this command.
	 * 
	 * @return the trust level.
	 */
	protected abstract int getTrustLevel();

	/**
	 * Creates a list of space delimited elements.
	 * 
	 * @param options
	 *            - a list of options
	 * @return the list
	 */
	protected final String getList(String... options) {
		StringBuilder result = new StringBuilder(options[0]);

		for (int i = 1; i < options.length; i++) {
			result.append(" ").append(options[i]);
		}

		return result.toString();
	}

	/**
	 * Returns the name of the local machine (where the service is running).
	 * 
	 * @return the local machine name
	 */
	protected final String getLocalMachineName() {
		return localMachineName;
	}

	/**
	 * Sets up the command parser.
	 */
	protected void setupParser() {
		parser = new STAFCommandParser(0, false);
		parser.addOption(getCommandName(), 1, STAFCommandParser.VALUENOTALLOWED);
	}
}
