package slightlysain.jvtftp.request.handler.accepter;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Accepter {
	public void accept() throws FileNotFoundException, IOException;

}
