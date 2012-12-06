package slightlysain.mock;

import java.net.InetAddress;

import slightlysain.jvtftp.request.Request;

public class DummyRequest implements Request {

	public boolean isRead() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWrite() {
		// TODO Auto-generated method stub
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
