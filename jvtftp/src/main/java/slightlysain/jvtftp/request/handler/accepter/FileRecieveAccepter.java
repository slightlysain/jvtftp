package slightlysain.jvtftp.request.handler.accepter;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.groovy.RequestHandlerBinding;
import slightlysain.jvtftp.session.SessionFactory;

public class FileRecieveAccepter extends AbstractAccepter implements Accepter {
	private String filename;
	private StreamFactory streamFactory;
	private Request request;

	public FileRecieveAccepter(RequestHandlerBinding binding, String filename) {
		super(binding);
		this.filename = filename;
		this.streamFactory = binding.getStreamFactory();
	}
	
	public void accept() throws FileNotFoundException {
		OutputStream fos = null;
		fos = streamFactory.getFileOutputStream(filename);
		accept(fos);
	}
}
