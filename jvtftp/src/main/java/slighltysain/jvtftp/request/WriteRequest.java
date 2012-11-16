package slighltysain.jvtftp.request;

import java.net.InetAddress;

public class WriteRequest implements Request {
	
	public WriteRequest() {
		
	}

	public boolean isRead() {
		return false;
	}

	public boolean isWrite() {
		return true;
	}

	public boolean isNetASCII() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return 0;
	}

}
