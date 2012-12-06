package slightlysain.jvtftp.request;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPRequestPacket;

public class WriteRequest extends RequestAbstract {

	public WriteRequest(InetAddress c, int port, String file, boolean netASCII) {
		super(c, port, file, netASCII);
	}
	
	public WriteRequest(TFTPRequestPacket requestPacket) {
		super(requestPacket);
	}

	public boolean isRead() {
		return false;
	}

	public boolean isWrite() {
		return true;
	}
}
