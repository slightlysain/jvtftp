package slightlysain.jvtftp.request.handler.accepter;

import groovy.lang.Closure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.io.OutputClosureRunner;
import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.groovy.AbstractScript;
import slightlysain.jvtftp.request.handler.groovy.RequestHandlerBinding;
import slightlysain.jvtftp.session.SessionFactory;

public class ClosureSendAcceptor extends AbstractAccepter implements Accepter {
	private Request request;
	private Closure<?> closure;
	private OutputClosureRunner outClosureRunner;
	private AbstractScript script;
	private ExecutorService executorService;
	private Logger log = LoggerFactory.getLogger(getClass());

	public ClosureSendAcceptor(RequestHandlerBinding binding, Closure<?> closure) {
		super(binding);
		executorService = binding.getExecutorService();
		this.closure = closure;
		script = (AbstractScript) closure.getOwner();
	}
	
	public void accept() throws IOException {
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream inputStream = new PipedInputStream(outputStream);
		script.setPrintStream(new PrintStream(outputStream));
		OutputClosureRunner runnable = new OutputClosureRunner(closure, outputStream);
		executorService.execute(runnable);
		accept(inputStream);
		inputStream.close();
		script.resetPrintStream();
	}
}
