package slightlysain.jvtftp.packetadapter;

import org.apache.commons.net.tftp.TFTPAckPacket;
import org.apache.commons.net.tftp.TFTPPacket;

public class AckPacketImpl extends AbstractPacketAdapter implements AckPacket {
	@Override
	public boolean isAckowledgment() {
		return true;
	}

	private TFTPAckPacket packet;
	
	public AckPacketImpl(TFTPPacket p) {
		super(p);
		packet = (TFTPAckPacket) p;
	}
	
	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.packetadapter.AckPacket#getBlockNumber()
	 */
	public int getBlockNumber() {
		return packet.getBlockNumber();
	}
}
