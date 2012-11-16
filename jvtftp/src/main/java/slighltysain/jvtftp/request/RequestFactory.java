package slighltysain.jvtftp.request;

import org.apache.commons.net.tftp.TFTPPacket;

public class RequestFactory {
	public Request newRequest(TFTPPacket p) throws NotRequestPacketException {
		int packetType = p.getType();
		if (TFTPPacket.READ_REQUEST == packetType) {
			ReadRequest req = new ReadRequest()
			return null;
		} else if (TFTPPacket.WRITE_REQUEST == packetType) {
			return null;
		} else {
			throw new NotRequestPacketException();
		}
	}
}
