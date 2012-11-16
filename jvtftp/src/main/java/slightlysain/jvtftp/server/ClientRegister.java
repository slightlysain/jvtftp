package slightlysain.jvtftp.server;

import java.net.InetAddress;

public interface ClientRegister {
	
	public abstract boolean contains(InetAddress address, int port);
	public abstract boolean register(InetAddress address, int port);
	public abstract void unregister(InetAddress address, int port);
	
}
