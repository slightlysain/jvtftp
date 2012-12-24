package slightlysain.jvtftp.io;

import groovy.lang.Closure;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputClosureRunner implements Runnable, ClosureRunner {
	private Closure<?> clos;
	private InputStream input;
	private Logger log = LoggerFactory.getLogger(getClass());
	private Exception exception = null;

	public InputClosureRunner(Closure<?> clos, InputStream input) {
		this.clos = clos;
		this.input = input;
	}

	public Exception exception() {
		return exception;

	}

	public void run() {
		clos.call(input);
		try {
			input.close();
		} catch (IOException e) {
			exception = e;
		}
	}
}
