package slightlysain.jvtftp.request;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.session.SessionController;

public abstract class RequestAbstract implements Request {
	private String file;
	private InetAddress client;
	private int port;
	private long requesttime;
	private boolean netASCII;

	Logger log = LoggerFactory.getLogger(getClass());

	public RequestAbstract(InetAddress c, int port, String file, boolean netASCII) {
		requesttime = System.currentTimeMillis();
		this.client = c;
		this.port = port;
		this.file = file;
		this.netASCII = netASCII;
	}

	public RequestAbstract(TFTPRequestPacket requestPacket) {
		requesttime = System.currentTimeMillis();
		this.client = requestPacket.getAddress();
		this.port = requestPacket.getPort();
		this.file = requestPacket.getFilename();
		this.netASCII = (TFTP.ASCII_MODE == requestPacket.getMode());
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
