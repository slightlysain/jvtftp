package slightlysain.jvtftp.session;

public interface Session {
	
	public boolean isRead();
	public boolean isWrite();
	public void accept();
	public void decline();
	
}
