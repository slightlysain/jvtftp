package slightlysain.jvtftp.tftpadapter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.net.tftp.TFTPPacketException;

public interface TFTPAdapter {

	public abstract void setPacketHandler(OnPacketHandler packetHandler);

	public abstract void setTimeoutHandler(OnTimeoutHandler timeoutHandler);

	public abstract void sendDataPacket(InetAddress clientAddress, int port,
			byte[] by, int block) throws IOException;

	public abstract void sendErrorPacket(InetAddress clientAddress, int port,
			int code, String msg) throws IOException;

	public abstract void sendAckPacket(InetAddress clientAddress, int port,
			int block) throws IOException;

	public abstract boolean isOpen();

	public abstract void open(int port) throws SocketException;

	public abstract void close();

	public abstract void listen() throws IOException, TFTPPacketException;

}