package slighltysain.jvtftp.request;

import java.net.InetAddress;

public class ReadRequest implements Request {
	
	public ReadRequest(int port, String file, )

	public boolean isRead() {
		return true;
	}

	public boolean isWrite() {
		return false;
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
