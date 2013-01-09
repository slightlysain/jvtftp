package slightlysain.jvtftp.request;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.session.SessionController;

public abstract class AbstractRequest implements Request {
	private String file;
	private InetAddress client;
	private int port;
	private long requesttime;
	private boolean netASCII;
	private int type;

	Logger log = LoggerFactory.getLogger(getClass());

	public AbstractRequest(InetAddress c, int port, String file,
			boolean netASCII, int type) {
		requesttime = System.currentTimeMillis();
		this.client = c;
		this.port = port;
		this.file = file;
		this.netASCII = netASCII;
		this.type = type;
	}

	public boolean isRead() {
		return (type == TFTPRequestPacket.READ_REQUEST);
	}

	public boolean isWrite() {
		return (type == TFTPRequestPacket.WRITE_REQUEST);
	}

	public AbstractRequest(TFTPRequestPacket requestPacket) {
		requesttime = System.currentTimeMillis();
		this.client = requestPacket.getAddress();
		this.port = requestPacket.getPort();
		String filename = requestPacket.getFilename();
		if (filename.startsWith("/")) {
			this.file = filename.substring(1);
		} else {
			this.file = filename;
		}
		this.netASCII = (TFTP.ASCII_MODE == requestPacket.getMode());
		this.type = requestPacket.getType();
	}

	public boolean isNetASCII() {
		return netASCII;
	}

	public String getFilename() {
		return file;
	}

	public InetAddress getAddress() {
		return client;
	}

	public int getPort() {
		return port;
	}

	public long getTime() {
		return requesttime;
	}

	public String toString() {
		return file + ":" + client.getHostAddress() + ":" + port;
	}

}
