package slightlysain.jvtftp.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SessionController {
	public void error(int errorid, String message);	
	public void send(InputStream out);
	public void recieve(OutputStream in);
}
