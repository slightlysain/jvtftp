package slightlysain.jvtftp.packetadapter;

import org.apache.commons.net.tftp.TFTPPacket;

public interface PacketAdapterFactory {
	public PacketAdapter newAdapter(TFTPPacket packet);
}