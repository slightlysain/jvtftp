package slightlysain.jvtftp.request.handler.groovy;

import java.util.concurrent.ExecutorService;

import groovy.lang.Binding;
import slightlysain.jvtftp.configuration.Configuration;
import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.session.SessionFactory;

public class RequestHandlerBinding extends Binding {
	private Request request;
	private GroovyRequestHandler handler;
	private StreamFactory streamFactory;
	private boolean handled;
	private SessionFactory sessionFactory;
	private ExecutorService executor;
	private Configuration configuration;

	public RequestHandlerBinding(Request r, GroovyRequestHandler h,
			StreamFactory streamFactory, SessionFactory sessionFactory,
			ExecutorService executor, Configuration configuration) {
		this.request = r;
		this.handler = h;
		this.handled = false;
		this.streamFactory = streamFactory;
		this.sessionFactory = sessionFactory;
		this.executor = executor;
		this.configuration = configuration;
	}

	public StreamFactory getStreamFactory() {
		return streamFactory;
	}

	public Request getRequest() {
		return request;
	}

	public GroovyRequestHandler getHandler() {
		return handler;
	}

	public boolean hasHandled() {
		return handled;
	}

	public void setHandled(boolean handled) {
		this.handled = handled;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("request")) {
			return request;
		} else if (name.equals("handler")) {
			return handler;
		} else if (name.equals("getpriority")) {
			return false;
		} else if (name.equals("streamFactory")) {
			return streamFactory;
		} else if (configuration.contains(name.replace('_', '.'))) {
			return configuration.getProperty(name.replace('_', '.'));
		} else {
			return super.getVariable(name);
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public ExecutorService getExecutorService() {
		return executor;
	}
}
