package slightlysain.jvtftp.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamFactory {

	public abstract InputStream getFileInputStream(String filename)
			throws FileNotFoundException;

	public abstract OutputStream getFileOutputStream(String filename)
			throws FileNotFoundException;
	
	public abstract File getOutputFile(String filename);
	
	public abstract File getInputFile(String filename);


}