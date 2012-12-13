package slightlysain.mock;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import slightlysain.jvtftp.streamfactory.StreamFactory;;

public class MockStreamFactory implements StreamFactory {
	OutputStream out;
	InputStream in;
	

	public MockStreamFactory(OutputStream outstream, InputStream instream) {
		super();
		this.out = out;
		this.in = in;
	}

	public InputStream getFileInputStream(String filename)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return in;
	}

	public OutputStream getFileOutputStream(String filename)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return out;
	}

}
