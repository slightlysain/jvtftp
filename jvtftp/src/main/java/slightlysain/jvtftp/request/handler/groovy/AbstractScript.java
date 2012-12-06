package slightlysain.jvtftp.request.handler.groovy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.RequestHandler;
import groovy.lang.Closure;
import groovy.lang.Script;

public abstract class AbstractScript extends Script {

	private RequestHandlerBinding binding;
	private Request request;
	private GroovyRequestHandler handler;
	private PrintStream out = System.out;
	private boolean setup = false;
	private Logger log = LoggerFactory.getLogger(getClass());
	
//	public AbstractGroovyRequestScript() {}
//	
//	
//	public AbstractGroovyRequestScript(Request r, GroovyRequestHandler hand) {
//		// use for dependency injection when testing
//		request = r;
//		handler = hand;
//		setup = true;
//	}
	
	private void setBinding() {
		binding = (RequestHandlerBinding) getBinding();
	}

	private void setRequest() {
		request = binding.getRequest();
	}

	private void setHandler() {
		handler = binding.getHandler();
	}

	private void setup() {
		if(!setup) {
			setBinding();
			setRequest();
			setHandler();
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

	public void accept(String filename) {
		System.out.println("accepted");
		setup();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			log.error("could not find file");
			e.printStackTrace();
		}
		handler.accept(fis, request);
		setHandled();
	}
	
	public void accept(Closure<?> clos) {
		setup();
		PrintStream tmp = out;
		PipedOutputStream outputStream = new PipedOutputStream();
		try {
			PipedInputStream  inputStream = new PipedInputStream(outputStream);
			out = new PrintStream(outputStream);
			clos.call();
			out.close();
			outputStream.close();
			out = tmp;
			handler.accept(inputStream, request);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setHandled();
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
