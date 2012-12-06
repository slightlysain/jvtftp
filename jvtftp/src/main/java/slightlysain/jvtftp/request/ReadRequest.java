package slightlysain.jvtftp.request;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPRequestPacket;

public class ReadRequest extends RequestAbstract {

	public ReadRequest(TFTPRequestPacket requestPacket) {
		super(requestPacket);
	}

	public ReadRequest(InetAddress c, int port, String file, boolean netASCII) {
		super(c, port, file, netASCII);
	}

	public boolean isRead() {
		return true;
	}

	public boolean isWrite() {
		return false;
	}
}
