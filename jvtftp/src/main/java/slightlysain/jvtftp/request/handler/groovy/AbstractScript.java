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
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.io.OutputClosureRunner;
import slightlysain.jvtftp.io.stream.JvtftpInput;
import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.accepter.AbstractAccepter;
import slightlysain.jvtftp.request.handler.accepter.ClosureRecieveAcceptor;
import slightlysain.jvtftp.request.handler.accepter.ClosureSendAcceptor;
import slightlysain.jvtftp.request.handler.accepter.FileRecieveAccepter;
import slightlysain.jvtftp.request.handler.accepter.FileSendAcceptor;

public abstract class AbstractScript extends Script {

	private RequestHandlerBinding binding;
	private Request request;
	private GroovyRequestHandler handler;
	private PrintStream out = System.out;
	private boolean outNeedsToClose = false;
	private boolean setup = false;
	private Logger log = LoggerFactory.getLogger(getClass());
	private boolean handled = false;

	private void setup() {
		if (!setup) {
			binding = (RequestHandlerBinding) getBinding();
			request = binding.getRequest();
			handler = binding.getHandler();
			setup = true;
		}
	}

	private void setHandled() {
		handled = true;
		binding.setHandled(true);
	}

	public PrintStream getOut() {
		return out;
	}

	public AbstractScript getScript() {
		return this;
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
		FileSendAcceptor accepter = new FileSendAcceptor(binding, filename);
		accepter.accept();
		setHandled();
	}

	private void recieveAccept(String filename) throws FileNotFoundException {
		setup();
		FileRecieveAccepter accepter = new FileRecieveAccepter(binding,
				filename);
		accepter.accept();
		setHandled();
	}

	public void setPrintStream(PrintStream out) {
		setup();
		this.out = out;
		binding.setVariable("out", out);
		outNeedsToClose = true;
	}

	public void resetPrintStream() {
		setup();
		if (outNeedsToClose) {
			out.close();
			outNeedsToClose = false;
		}
		out = System.out;
		binding.setVariable("out", out);
	}

	public void sendAccept(Closure<?> clos) throws IOException {
		setup();
		ClosureSendAcceptor accepter = new ClosureSendAcceptor(binding, clos);
		accepter.accept();
		setHandled();
	}

	public void recieveAccept(Closure<?> clos) throws IOException {
		setup();
		ClosureRecieveAcceptor accepter = new ClosureRecieveAcceptor(binding,
				clos);
		accepter.accept();
		setHandled();
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
		ExecutorService executor = binding.getExecutorService();
		if (request.isRead()) {
			executor.execute(inputStream);
			handler.accept(inputStream.getInput(), request);
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
