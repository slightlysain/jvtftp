package slightlysain.jvtftp.request.handler.groovy;

import java.io.PrintWriter;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.stream.StreamFactory;
import groovy.lang.Binding;

public class RequestHandlerBinding extends Binding {
	private Request request;
	private GroovyRequestHandler handler;
	private StreamFactory streamFactory;
	private boolean handled;

	public RequestHandlerBinding(Request r, GroovyRequestHandler h,
			StreamFactory streamFactory) {
		this.request = r;
		this.handler = h;
		this.handled = false;
		this.streamFactory = streamFactory;
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
		} else if(name.equals("streamFactory")) {
			return streamFactory;
		} else {
			return super.getVariable(name);
		}
	}
}
