package slightlysain.jvtftp.request.handler.accepter;

import java.io.FileNotFoundException;
import java.io.InputStream;

import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.groovy.RequestHandlerBinding;
import slightlysain.jvtftp.session.SessionFactory;

public class FileSendAcceptor extends AbstractAccepter implements Accepter {
	private String filename;
	private StreamFactory streamFactory;
	private Request request;

	public FileSendAcceptor(RequestHandlerBinding binding, String filename) {
		super(binding);
		this.filename = filename;
		this.streamFactory = binding.getStreamFactory();
	}

	public void accept() throws FileNotFoundException {
		InputStream fis = null;
		fis = streamFactory.getFileInputStream(filename);
		accept(fis);
	}
}
