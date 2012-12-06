package slightlysain.jvtftp.packetadapter;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPPacket;

public abstract class AbstractPacketAdapter implements PacketAdapter {
	private TFTPPacket packet;
	
	public AbstractPacketAdapter(TFTPPacket p) {
		packet = p;
	}
	
	public boolean isClient(InetAddress client, int port) {
		return client.equals(packet.getAddress()) && port == packet.getPort();
	}

	public InetAddress getClient() {
		return packet.getAddress();
	}

	public int getPort() {
		return packet.getPort();
	}

	public boolean isAckowledgment() {
		return false;
	}

	public boolean isData() {

		return false;
	}

	public boolean isReadRequest() {
		return false;
	}

	public boolean isWriteRequest() {
		return false;
	}

	public boolean isError() {
		return false;
	}

}
