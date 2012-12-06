package slightlysain.jvtftp.packetadapter;

import org.apache.commons.net.tftp.TFTPPacket;
import static org.apache.commons.net.tftp.TFTPPacket.*;

public class PacketAdapterFactoryImpl implements PacketAdapterFactory {
	
	public PacketAdapter newAdapter(TFTPPacket packet) {
		int type = packet.getType();
		switch(type) {
		case DATA:
			return new DataPacketImpl(packet);
		case ERROR:
			return new ErrorPacketImpl(packet);
		case ACKNOWLEDGEMENT:
			return new AckPacketImpl(packet);
		}
		throw new IllegalArgumentException();
	}
	
}
