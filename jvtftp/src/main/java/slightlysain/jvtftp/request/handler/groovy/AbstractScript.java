package slightlysain.jvtftp.request.handler.groovy;

import groovy.lang.Closure;
import groovy.lang.Script;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.stream.JvtftpInput;
import slightlysain.jvtftp.stream.StreamFactory;

public abstract class AbstractScript extends Script {

	private RequestHandlerBinding binding;
	private Request request;
	private GroovyRequestHandler handler;
	private PrintStream out = System.out;
	private boolean setup = false;
	private StreamFactory streamFactory;
	private Logger log = LoggerFactory.getLogger(getClass());
	private boolean handled = false;

	private void setup() {
		if (!setup) {
			binding = (RequestHandlerBinding) getBinding();
			request = binding.getRequest();
			handler = binding.getHandler();
			streamFactory = binding.getStreamFactory();
			setup = true;
		}
	}

	private void setHandled() {
		handled = true;
		binding.setHandled(true);
	}

	@Override
	public void println() {
		setup();
		out.println();
	}

	@Override
	public void print(Object value) {
		setup();
		out.print(value);
	}

	@Override
	public void println(Object value) {
		setup();
		out.println(value);
	}

	@Override
	public void printf(String format, Object value) {
		setup();
		out.printf(format, value);
	}

	@Override
	public void printf(String format, Object[] values) {
		setup();
		out.printf(format, values);
	}

	private void sendAccept(String filename) throws FileNotFoundException {
		setup();
		InputStream fis = null;
		try {
			fis = streamFactory.getFileInputStream(filename);
		} catch (FileNotFoundException e) {
			log.error("could not find file");
			throw e;
		}
		handler.accept(fis, request);
		setHandled();
	}

	private void recieveAccept(String filename) {
		setup();
		OutputStream fos = null;
		try {
			fos = streamFactory.getFileOutputStream(filename);
		} catch (FileNotFoundException e) {
			log.error("could not find file");
			e.printStackTrace();
		}
		handler.accept(fos, request);
		setHandled();
	}

	public void setPrintStream(PrintStream out) {
		this.out = out;
	}

	public void resetPrintStream() {
		out = System.out;
	}

	public void sendAccept(Closure<?> clos) throws IOException {
		setup();
		// change to byte array
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream inputStream = new PipedInputStream(outputStream);
		setPrintStream(new PrintStream(outputStream));
		clos.call();
		out.close();
		outputStream.close();
		resetPrintStream();
		handler.accept(inputStream, request);
		setHandled();
	}

	public void recieveAccept(Closure<?> clos) throws IOException {
		setup();
		// change to byte array
		PipedOutputStream outputStream = new PipedOutputStream();
		InputStream in = new PipedInputStream(outputStream);
		handler.accept(outputStream, request);
		clos.call(in);
		in.close();
		outputStream.close();
	}

	public void accept(Closure<?> clos) throws IOException {
		setup();
		if (handled) {
			return;
		}
		if (request.isRead()) {
			sendAccept(clos);
		} else if (request.isWrite()) {
			recieveAccept(clos);
		} else {
			log.error("Bad request recieved");
		}
	}

	public void accept(String filename) throws FileNotFoundException {
		setup();
		if (handled) {
			return;
		}
		if (request.isRead()) {
			sendAccept(filename);
		} else if (request.isWrite()) {
			recieveAccept(filename);
		} else {
			log.error("Bad request recieved");
		}
	}
	
	public void accept() throws FileNotFoundException {
		setup();
		if (handled) {
			return;
		}
		String filename = request.getFilename();
		if (request.isRead()) {
			sendAccept(filename);
		} else if (request.isWrite()) {
			recieveAccept(filename);
		} else {
			log.error("Bad request recieved");
		}
	}

	public void accept(JvtftpInput inputStream) throws FileNotFoundException,
			IOException, InterruptedException {
		setup();
		if (request.isRead()) {
			Thread t = new Thread(inputStream);
			t.start();
			handler.accept(inputStream.getInput(), request);
			t.join();
			setHandled();
		} else if (request.isWrite()) {
			log.error("Cannot respond to write request with inputstream");
		}
	}

	public void deny() {
		if (handled) {
			return;
		}
		setup();
		handler.deny(request);
		setHandled();
	}

	public void deny(int error, String message) {
		if (handled) {
			return;
		}
		setup();
		handler.deny(request, error, message);
		setHandled();
	}

	public void skip() {
		if (handled) {
			return;
		}
		setup();
		handler.skip(request);
		setHandled();
	}
}
