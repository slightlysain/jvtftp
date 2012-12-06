package slightlysain.jvtftp.request;

import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPRequestPacket;

public class RequestFactory {
	public static Request newRequest(TFTPPacket p) throws NotRequestPacketException {
		int packetType = p.getType();
		if (TFTPPacket.READ_REQUEST == packetType) {
			ReadRequest req = new ReadRequest((TFTPRequestPacket) p);
			return req;
		} else if (TFTPPacket.WRITE_REQUEST == packetType) {
			WriteRequest req = new WriteRequest((TFTPRequestPacket) p);
			return req;
		} else {
			throw new NotRequestPacketException();
		}
	}
}
