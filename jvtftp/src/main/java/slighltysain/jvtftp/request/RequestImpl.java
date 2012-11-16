package slighltysain.jvtftp.request;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.session.Session;

public class RequestImpl implements Request {
	private String file;
	private InetAddress client;
	private long requesttime;
	private int connectionTransferFormat;
	private TransferFlow iodirection;
	private Session session;
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	public enum TransferFlow { READ, WRITE };
	
	public RequestImpl(InetAddress c, String file, int connectionTransferFormat, TransferFlow iodirection) {
		this.client = c;
		this.file = file;
		this.connectionTransferFormat = connectionTransferFormat;
		this.iodirection = iodirection;
		requesttime = System.currentTimeMillis();
	}
	
	public RequestImpl(TFTPRequestPacket requestPacket) {
		requesttime = System.currentTimeMillis();
		//TFTPRequestPacket requestPacket = (TFTPRequestPacket) packet;
		this.client = requestPacket.getAddress();
		this.file = requestPacket.getFilename();
		this.connectionTransferFormat = requestPacket.getMode();
		if(requestPacket.getType() == TFTPRequestPacket.READ_REQUEST)
				this.iodirection = TransferFlow.READ;
		else if(requestPacket.getType() == TFTPRequestPacket.WRITE_REQUEST) {
			this.iodirection = TransferFlow.WRITE;
		} else {
			//error
			//TODO:throw not request exception
		}	
	}
	
	public boolean isNetASCII() {
		return (TFTP.NETASCII_MODE == connectionTransferFormat);
	}
	
	public void setSession(Session aNewSession) {
		this.session = aNewSession;
	}
	
	public Session getSession() {
		return session;
	}
	
	public void setResponseBytes(byte[] b) {
		session.setResponseByteArray(b);
	}
	
	public byte[] getRecievedBytes() {
		waitForClosedSession();
		return session.getRecieveBytes();
	}
	
	public TransferFlow getTransferDirection() {
		return iodirection;
	}
	
	public String getFile() { 
		return file;
	}
	
	public String getIP() {
		return client.getHostAddress();
	}
	
	public InetAddress getInetAddress() {
		return client;
	}
	
	public long getRequestTime() {
		return requesttime;
	}
	
	@Override
	public String toString() {
		return ("IP:" + client.getHostAddress() + ",File:" + file + ",Time:" + requesttime);
	}

	public boolean isRead() {
		return (TransferFlow.READ == iodirection);
	}
	
	public boolean isWrite() {
		return (TransferFlow.WRITE == iodirection);
	}
	
	public void accept() {
		if(session.encounteredError() || session.isClosed()) {
			log.error("Cannot except session has encounterd error");
			return;
		}
		session.accept();
	}
	
	public void deny() {
		session.deny();
	}
	
	public void waitForClosedSession() {
		while(!session.isClosed()) {
			//LOOP
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				log.error("Problem with sleeping thread");
			}
		}
	}
	
	public boolean encounteredError() {
		waitForClosedSession();
		return session.encounteredError();
	}
	
	public void respondFile(String file) {
		String dir = FileAdapter.FILE_DIR + file;
		setResponseBytes(FileAdapter.fileToByteArray(dir));
		accept();
	}
	
	public void saveResponse(String file) {
		accept();
		waitForClosedSession();
		String dir = FileAdapter.FILE_DIR + file;
		byte[] data = session.getRecieveBytes();
		FileAdapter.byteArrayToFile(data, dir);
	}

	public String getFilename() {
		// TODO Auto-generated method stub
		return null;
	}

	public InetAddress getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getTime() {
		return requesttime;
	}
	
}
