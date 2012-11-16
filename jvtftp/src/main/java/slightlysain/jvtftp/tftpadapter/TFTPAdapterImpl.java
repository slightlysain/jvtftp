package slightlysain.jvtftp.tftpadapter;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPAckPacket;
import org.apache.commons.net.tftp.TFTPDataPacket;
import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TFTPAdapterImpl implements TFTPAdapter {

	private TFTP tftp = new TFTP();
	private boolean exit = false;
	private TFTPPacket packet;
	private OnPacketHandler packetHandler;
	private OnTimeoutHandler timeoutHandler;

	Logger LOG = LoggerFactory.getLogger(TFTPAdapterImpl.class);

	public TFTPAdapterImpl() {
		packetHandler = new NullPacketHandler();
		timeoutHandler = new NullTimeoutHandler();
	}
	
	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#setPacketHandler(slightlysain.jvtftp.tftpadapter.OnPacketHandler)
	 */
	public void setPacketHandler(OnPacketHandler packetHandler) {
		this.packetHandler = packetHandler;
	}
	
	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#setTimeoutHandler(slightlysain.jvtftp.tftpadapter.OnTimeoutHandler)
	 */
	public void setTimeoutHandler(OnTimeoutHandler timeoutHandler) {
		this.timeoutHandler = timeoutHandler;
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#sendDataPacket(java.net.InetAddress, int, byte[], int)
	 */
	public void sendDataPacket(InetAddress clientAddress, int port, byte[] by,
			int block) throws IOException {
		TFTPDataPacket datapacket = new TFTPDataPacket(clientAddress, port,
				block, by);
		tftp.send(datapacket);
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#sendErrorPacket(java.net.InetAddress, int, int, java.lang.String)
	 */
	public void sendErrorPacket(InetAddress clientAddress, int port, int code,
			String msg) throws IOException {
		TFTPErrorPacket errorpacket = new TFTPErrorPacket(clientAddress, port,
				code, msg);
		tftp.send(errorpacket);
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#sendAckPacket(java.net.InetAddress, int, int)
	 */
	public void sendAckPacket(InetAddress clientAddress, int port, int block)
			throws IOException {
		TFTPAckPacket ackpacket = new TFTPAckPacket(clientAddress, port, block);
		tftp.send(ackpacket);
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#isOpen()
	 */
	public boolean isOpen() {
		return tftp.isOpen();
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#open(int)
	 */
	public void open(int port) throws SocketException {
		tftp.open(port);
		LOG.info("port bind on " + tftp.getLocalPort());
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#close()
	 */
	public void close() {
		exit = true;
		tftp.close();
		LOG.trace("packet adapter closed");
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.tftpadapter.TFTPAdapter#listen()
	 */
	public void listen() throws IOException,
			TFTPPacketException {
		packet = null;
		// accept incoming packets
		while (!exit) {
			// eventAction.preReceive();
			try {
				packet = tftp.receive();
				packetHandler.onPacket(packet);
			} catch (InterruptedIOException e) {
				timeoutHandler.onTimeout();
			} catch (SocketException e) {
				timeoutHandler.onTimeout();
			}
		}
	}
}
