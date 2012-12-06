package slightlysain.jvtftp.session;

import static org.apache.commons.net.tftp.TFTPErrorPacket.UNDEFINED;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.packetadapter.AckPacket;
import slightlysain.jvtftp.packetadapter.PacketAdapter;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;

public class SendSession extends AbstractSession {
	private int expectedBlock;
	private PacketAdapter pck;
	private AckPacket ackpacket;
	private TFTPAdapter tftpadapter;
	private Chunker chunker;

	private Logger log = LoggerFactory.getLogger(getClass());

	SendSession(TFTPAdapter tftpadapter, InetAddress clientAddress, int port,
			Chunker chunker, PacketAdapterFactory packetFactory) {
		super(packetFactory, clientAddress, port, tftpadapter);
		if (null == chunker) {
			throw new NullPointerException("chunker null");
		}
		this.tftpadapter = tftpadapter;
		this.chunker = chunker;
	}

	public void onTimeout() {
		log.error("timeout occured");
	}

	@Override
	protected void onSafePacket(PacketAdapter packet) {
		pck = packet;
		if (pck.isAckowledgment()) {
			ackpacket = (AckPacket) pck;
			ackPacket();
		} else {
			log.error("unexpected packet type recieved");
			tftpadapter.close();
		}
	}

	public void start() throws IOException {
		if (chunker.hasNextByte()) {
			expectedBlock = 1;
			byte[] bytes = chunker.getNext();
			sendData(bytes, 1);
		}
	}

	private void ackPacket() {
		if (isExpectedBlockAck()) {
			if (chunker.hasNextByte()) {
				sendNextBlock();
			} else {
				// no more blocks so close adapter
				tftpadapter.close();
			}
		} else if (isPreviousBlockAck()) {
			log.error("acknowledgment not for expected block number:"
					+ expectedBlock
					+ " instead, acknowledgment recieved for block number:"
					+ ackpacket.getBlockNumber());
		} else if (isGreaterAckThanBlock()) {
			log.error("out of order acknowledgment recieved");
		} else {
			log.error("unexpected problem occured");
			tftpadapter.close();
		}
	}

	private boolean isGreaterAckThanBlock() {
		return ackpacket.getBlockNumber() > expectedBlock;
	}

	private void sendNextBlock() {
		int block = ++expectedBlock;
		byte[] bytes;
		try {
			bytes = chunker.getNext();
			sendData(bytes, block);
		} catch (IOException e) {
			try {
				log.error(
						"IOExeption when reading file, sending error packet to client",
						e);
				sendError(UNDEFINED, "IOException");
			} catch (IOException e1) {
				log.error("IOException could not send error packet to client",
						e1);
			}
			tftpadapter.close();
		}
	}

	private boolean isExpectedBlockAck() {
		return ackpacket.getBlockNumber() == expectedBlock;
	}

	private boolean isPreviousBlockAck() {
		return ackpacket.getBlockNumber() < expectedBlock;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see slightlysain.jvtftp.session.Session#onQuit()
	 */
	public void onQuit() {
		log.error("on quit occured");
	}

}
