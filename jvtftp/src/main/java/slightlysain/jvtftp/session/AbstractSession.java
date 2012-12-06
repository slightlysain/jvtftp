package slightlysain.jvtftp.session;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.packetadapter.ErrorPacket;
import slightlysain.jvtftp.packetadapter.PacketAdapter;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;

public abstract class AbstractSession implements Session {

	private PacketAdapter packet;
	private PacketAdapterFactory packetFactory;
	private Logger log = LoggerFactory.getLogger(getClass());
	private InetAddress clientAddress;
	private int port;
	private TFTPAdapter tftpadapter;

	public AbstractSession(PacketAdapterFactory packetFactory,
			InetAddress clientAddress, int port, TFTPAdapter tftpadapter) {
		if (null == packetFactory) {
			throw new NullPointerException("packet factory null");
		} else if (null == clientAddress) {
			throw new NullPointerException("client address null");
		} else if (null == tftpadapter) {
			throw new NullPointerException("tftpadapter null");
		}
		this.packetFactory = packetFactory;
		this.clientAddress = clientAddress;
		this.port = port;
		this.tftpadapter = tftpadapter;
		tftpadapter.setPacketHandler(this);
		tftpadapter.setTimeoutHandler(this);
	}

	private void errorPacket() {
		ErrorPacket errorpacket = (ErrorPacket) packet;
		log.error("Error packet recieved code:" + errorpacket.getCodeString()
				+ " message:" + errorpacket.getMessage());
		tftpadapter.close();
	}

	public void onPacket(TFTPPacket tftppacket) {
		packet = packetFactory.newAdapter(tftppacket);
		if (!packet.isClient(clientAddress, port)) {
			log.error("packet is not from this sessions client");
		} else if (packet.isError()) {
			errorPacket();
		} else {
			onSafePacket(packet);
		}
	}

	protected void sendError(int code, String message) throws IOException {
		tftpadapter.sendErrorPacket(clientAddress, port, code, message);
	}

	protected void sendData(byte[] bytes, int block) throws IOException {
		tftpadapter.sendDataPacket(clientAddress, port, bytes, block);
	}

	protected void sendAck(int block) throws IOException {
		tftpadapter.sendAckPacket(clientAddress, port, block);
	}

	protected abstract void onSafePacket(PacketAdapter packet);
}
