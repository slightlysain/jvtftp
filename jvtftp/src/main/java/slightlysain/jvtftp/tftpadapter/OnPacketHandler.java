package slightlysain.jvtftp.tftpadapter;

import org.apache.commons.net.tftp.TFTPPacket;

public interface OnPacketHandler {
	public void onPacket(TFTPPacket p);
}
