package slightlysain.jvtftp.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamFactoryImpl implements StreamFactory {

	private String incomingDir;
	private String outgoingDir;

	public StreamFactoryImpl(String incomingDir, String outgoingDir) {
		this.incomingDir = incomingDir;
		this.outgoingDir = outgoingDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slightlysain.jvtftp.streamfactory.StreamFactory#getFileInputStream(java
	 * .lang.String)
	 */
	public InputStream getFileInputStream(String filename)
			throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(outgoingDir
				+ File.separatorChar + filename);
		return fis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slightlysain.jvtftp.streamfactory.StreamFactory#getFileOutputStream(java
	 * .lang.String)
	 */
	public OutputStream getFileOutputStream(String filename)
			throws FileNotFoundException {
		FileOutputStream fos = new FileOutputStream(incomingDir
				+ File.separatorChar + filename);
		return fos;
	}

	public File getOutputFile(String filename) {
		File file = new File(incomingDir + File.separatorChar + filename);
		return file;
	}

	public File getInputFile(String filename) {
		File file = new File(outgoingDir + File.separatorChar + filename);
		return file;
	}

}
