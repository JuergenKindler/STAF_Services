package org.jki.staf.service.irc;

import java.util.List;

import org.schwering.irc.lib.IRCConnection;

public interface ConnectionHolder {

	public void add(IRCConnection connection);

	public boolean remove(IRCConnection connection);

	public List<IRCConnection> getConnections();

}