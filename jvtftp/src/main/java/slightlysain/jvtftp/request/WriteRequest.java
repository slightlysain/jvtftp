package slightlysain.jvtftp.request;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPRequestPacket;

public class WriteRequest extends AbstractRequest {

	public WriteRequest(InetAddress c, int port, String file, boolean netASCII) {
		super(c, port, file, netASCII, TFTPRequestPacket.WRITE_REQUEST);
	}
	
	public WriteRequest(TFTPRequestPacket requestPacket) {
		super(requestPacket);
	}
}
