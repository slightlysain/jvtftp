package slightlysain.jvtftp.io;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Closure;

public class OutputClosureRunner implements Runnable, ClosureRunner {
	private Closure<?> clos;
	private OutputStream output;
	private Logger log = LoggerFactory.getLogger(getClass());
	private Object closarg = null;
	private Exception exception = null;

	public OutputClosureRunner(Closure<?> clos, OutputStream output) {
		this.clos = clos;
		this.output = output;
	}

	public void setClosarg(Object o) {
		closarg = o;
	}
	
	public Exception exception() {
		return exception;
		
	}

	public void run() {
		if(null == closarg) {
			clos.call();
		} else {
			clos.call(closarg);
		}
		try {
			output.close();
		} catch (IOException e) {
			exception = e;
			log.error("Problem closing output stream", e);
		}
	}
}
