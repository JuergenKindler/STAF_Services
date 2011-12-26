package org.jki.staf.service.irc.commands;

import org.jki.staf.service.commands.AbstractServiceCommand;
import org.jki.staf.service.commands.ServiceCommand;
import org.jki.staf.service.irc.ConnectionHolder;

import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.InitInfo;
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo;

public class CreateServerCommand extends AbstractServiceCommand implements
		ServiceCommand {
	private ConnectionHolder connectionHolder;
	private static final String HOST = "HOST";
	private static final String PORT = "PORT";
	private static final String PORT_MIN = "PORTMIN";
	private static final String PORT_MAX = "PORTMAX";
	private static final String PASS_WORD = "PASSWORD";
	private static final String NICK_NAME = "NICKNAME";
	private static final String USER = "USER";
	private static final String REAL_NAME = "REALNAME";
	private static final String SSL = "SSL";

	public CreateServerCommand(String commandName, String machineName,
			InitInfo initInfo, ConnectionHolder newConnectionHolder) {
		super(commandName, machineName, initInfo);
		connectionHolder = newConnectionHolder;
	}


	/** {@inheritDoc}*/
	@Override
	public STAFResult execute(final RequestInfo reqInfo) {
		STAFResult result = super.execute(reqInfo);
		
		if (result.rc == STAFResult.Ok) {
			// 
		}
	
		return result;
	}


	/** {@inheritDoc}*/
	@Override
	protected int getTrustLevel() {
		return 5;
	}


	/** {@inheritDoc}*/
	@Override
	protected void setupParser() {
		super.setupParser();
		parser.addOption(HOST, 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption(PORT, 15, STAFCommandParser.VALUEREQUIRED);
		parser.addOption(PORT_MIN, 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption(PORT_MAX, 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption(PASS_WORD, 1, STAFCommandParser.VALUEALLOWED);
		parser.addOption(NICK_NAME, 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption(USER, 1, STAFCommandParser.VALUEREQUIRED);
		parser.addOption(REAL_NAME, 1, STAFCommandParser.VALUEALLOWED);
		parser.addOption(SSL, 1, STAFCommandParser.VALUEALLOWED);
		
		String withPortList = getList(HOST, PORT, PASS_WORD, NICK_NAME, USER, REAL_NAME, SSL);
		String withPortRange = getList(HOST, PORT_MIN, PORT_MAX, PASS_WORD, NICK_NAME, USER, REAL_NAME, SSL);
		
		parser.addOptionGroup(withPortList, 1, 1);
		parser.addOptionGroup(withPortRange, 1, 1);
		
		parser.addOptionNeed(withPortList, CREATE);
		parser.addOptionNeed(withPortRange, CREATE);
	}


	@Override
	public String getCommandHelp() {
		return "CREATE";
	}
}
