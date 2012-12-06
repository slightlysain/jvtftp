package slightlysain.jvtftp.session;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.packetadapter.PacketAdapter;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;

public class ErrorSession extends AbstractSession {
	private Logger log = LoggerFactory.getLogger(getClass());
	private int code;
	private String message;
	private TFTPAdapter tftpadapter;

	ErrorSession(PacketAdapterFactory packetFactory, InetAddress clientAddress,
			int port, TFTPAdapter tftpadapter, int code, String message) {
		super(packetFactory, clientAddress, port, tftpadapter);
		this.tftpadapter = tftpadapter;
		if(code < 0) {
			throw new IndexOutOfBoundsException();
		}
		this.code = code;
		if(null == message) {
			throw new NullPointerException();
		}
		this.message = message;
	}

	public void start() throws IOException {
		sendError(code, message);
		tftpadapter.close();
	}

	public void onTimeout() {
		log.error("timeout occured");
	}

	public void onQuit() {
		log.error("quit occured");
	}

	@Override
	protected void onSafePacket(PacketAdapter packet) {
		log.error("on safe packet occured");
	}

}
