package slightlysain.jvtftp.io.stream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public interface JvtftpInput extends Runnable {
	public abstract InputStream getInput();

	public abstract void go() throws FileNotFoundException, IOException;
}
