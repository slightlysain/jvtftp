package slightlysain.jvtftp.server;

import java.net.InetAddress;

import slightlysain.jvtftp.request.Request;

public interface ClientRegister {

	public abstract boolean contains(InetAddress address, int port);

	public abstract boolean contains(Request req);

	public abstract void register(InetAddress address, int port)
			throws ClientAlreadyRegisteredException;

	public abstract void register(Request req) throws ClientAlreadyRegisteredException;

	public abstract void unregister(InetAddress address, int port) throws ClientNotRegisteredException;

	public abstract void unregister(Request r) throws ClientNotRegisteredException;

}
