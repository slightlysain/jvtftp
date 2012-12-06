package slightlysain.jvtftp.session;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.packetadapter.DataPacket;
import slightlysain.jvtftp.packetadapter.PacketAdapter;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import static org.apache.commons.net.tftp.TFTPErrorPacket.UNDEFINED;

public class RecieveSession extends AbstractSession {
	private OutputStream output;
	private Logger log = LoggerFactory.getLogger(getClass());
	private DataPacket datapacket;
	private TFTPAdapter tftpadapter;
	private int expectedBlock;

	RecieveSession(PacketAdapterFactory packetFactory,
			InetAddress clientAddress, int port, TFTPAdapter tftpadapter,
			OutputStream output) {
		super(packetFactory, clientAddress, port, tftpadapter);
		this.output = output;
		this.tftpadapter = tftpadapter;
	}

	public void onTimeout() {
		log.error("timeout occured");
	}

	public void onQuit() {
		log.error("on quit occured");
	}

	@Override
	protected void onSafePacket(PacketAdapter packet) {
		if (packet.isData()) {
			datapacket = (DataPacket) packet;
			try {
				if (isExpectedPacket()) {
					datapacket();
				} else if(isDuplicatePacket()) {
					log.info("duplicate packet recieved");
				} else {
					log.info("unexpected block number recieved");
				}
			} catch (IOException e) {
				log.error("could not write data to stream", e);
				try {
					sendError(UNDEFINED, "IOException");
				} catch (IOException e1) {
					log.error("could not send error packet to client", e1);
				}
				tftpadapter.close();
			}
		} else {
			log.error("packet not data packet");
		}
	}


	private boolean isDuplicatePacket() {
		return expectedBlock > datapacket.getBlockNumber();
	}

	private void datapacket() throws IOException {
		byte[] data = datapacket.getData();
		output.write(data);
		int block = expectedBlock++;
		sendAck(block);
		if(data.length < 512) {
			// no more blocks of data to recive
			tftpadapter.close();
		}
	}

	private boolean isExpectedPacket() {
		return expectedBlock == datapacket.getBlockNumber();
	}
	
	public void start() throws IOException {
		expectedBlock = 1;
		sendAck(0);
	}

}
