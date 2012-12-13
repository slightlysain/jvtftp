package slightlysain.jvtftp.streamfactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamFactory {

	public abstract InputStream getFileInputStream(String filename)
			throws FileNotFoundException;

	public abstract OutputStream getFileOutputStream(String filename)
			throws FileNotFoundException;

}