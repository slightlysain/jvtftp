package slightlysain.jvtftp.packetadapter;

import java.util.Arrays;

import org.apache.commons.net.tftp.TFTPDataPacket;
import org.apache.commons.net.tftp.TFTPPacket;

public class DataPacketImpl extends AbstractPacketAdapter implements DataPacket {
	private TFTPDataPacket packet;

	public DataPacketImpl(TFTPPacket p) {
		super(p);
		packet = (TFTPDataPacket) p;
	}
	
	@Override
	public boolean isData() {
		return true;
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.packetadapter.DataPacket#getData()
	 */
	public byte[] getData() {
		byte[] data = packet.getData();
		int offset = packet.getDataOffset();
		int length = packet.getDataLength();
		byte[] actual = Arrays.copyOfRange(data, offset, offset + length);
		return actual;
	}
	
	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.packetadapter.DataPacket#getDataLength()
	 */
	public int getDataLength() {
		return packet.getDataLength();
	}
	
	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.packetadapter.DataPacket#getBlockNumber()
	 */
	public int getBlockNumber() {
		return packet.getBlockNumber();
	}
}
