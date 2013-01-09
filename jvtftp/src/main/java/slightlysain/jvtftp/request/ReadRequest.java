package slightlysain.jvtftp.request;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPRequestPacket;

public class ReadRequest extends AbstractRequest {

	public ReadRequest(TFTPRequestPacket requestPacket) {
		super(requestPacket);
	}

	public ReadRequest(InetAddress c, int port, String file, boolean netASCII) {
		super(c, port, file, netASCII, TFTPRequestPacket.READ_REQUEST);
	}
}
