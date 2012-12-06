package slightlysain.jvtftp.packetadapter;

import java.net.InetAddress;

public interface PacketAdapter {
	public boolean isAckowledgment();
	public boolean isData();
	public boolean isReadRequest();
	public boolean isWriteRequest();
	public boolean isError();
	public InetAddress getClient();
	public int getPort();
	public boolean isClient(InetAddress client, int port);
}
