package slightlysain.jvtftp.request;

import java.net.InetAddress;

public interface Request {
	public abstract boolean isRead();
	public abstract boolean isWrite();
	public abstract boolean isNetASCII();
	public abstract String getFilename();
	public abstract InetAddress getAddress();
	public abstract int getPort();
	public abstract long getTime();
}
