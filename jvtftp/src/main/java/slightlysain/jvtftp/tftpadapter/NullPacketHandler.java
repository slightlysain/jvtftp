package slightlysain.jvtftp.tftpadapter;

import org.apache.commons.net.tftp.TFTPPacket;

public class NullPacketHandler implements OnPacketHandler {
	public void onPacket(TFTPPacket p) {
		//do nothing
	}

}
