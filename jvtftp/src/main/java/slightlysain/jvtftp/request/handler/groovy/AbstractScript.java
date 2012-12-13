package slightlysain.jvtftp.request.handler.groovy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.RequestHandler;
import slightlysain.jvtftp.streamfactory.StreamFactory;
import groovy.lang.Closure;
import groovy.lang.Script;

public abstract class AbstractScript extends Script {

	private RequestHandlerBinding binding;
	private Request request;
	private GroovyRequestHandler handler;
	private PrintStream out = System.out;
	private boolean setup = false;
	private StreamFactory streamFactory;
	private Logger log = LoggerFactory.getLogger(getClass());

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

	public void accept() {
		setup();
	}

	private void sendAccept(String filename) {
		setup();
		InputStream fis = null;
		try {
			fis = streamFactory.getFileInputStream(filename);
		} catch (FileNotFoundException e) {
			log.error("could not find file");
			e.printStackTrace();
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

	public void sendAccept(Closure<?> clos) throws IOException {
		setup();
		PrintStream tmp = out;
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream inputStream = new PipedInputStream(outputStream);
		out = new PrintStream(outputStream);
		clos.call();
		out.close();
		outputStream.close();
		out = tmp;
		handler.accept(inputStream, request);
		setHandled();
	}

	public void recieveAccept(Closure<?> clos) throws IOException {
		setup();
		PipedOutputStream outputStream = new PipedOutputStream();
		InputStream in = new PipedInputStream(outputStream);
		handler.accept(outputStream, request);
		clos.call(in);
		in.close();
		outputStream.close();
	}

	public void accept(Closure<?> clos) throws IOException {
		setup();
		if (request.isRead()) {
			sendAccept(clos);
		} else if (request.isWrite()) {
			recieveAccept(clos);
		} else {
			log.error("Bad request recieved");
		}
	}

	public void accept(String filename) {
		setup();
		if (request.isRead()) {
			sendAccept(filename);
		} else if (request.isWrite()) {
			recieveAccept(filename);
		} else {
			log.error("Bad request recieved");
		}
	}

	public void deny() {
		setup();
		handler.deny(request);
		setHandled();
	}

	public void deny(int error, String message) {
		setup();
		handler.deny(request, error, message);
		setHandled();
	}

	public void skip() {
		setup();
		handler.skip(request);
	}
}
