package slightlysain.jvtftp.request.handler.groovy;

import java.io.PrintWriter;

import slightlysain.jvtftp.request.Request;
import groovy.lang.Binding;

public class RequestHandlerBinding extends Binding {
	private Request request;
	private GroovyRequestHandler handler;
	private PrintWriter out;
	private boolean handled;
	
	public PrintWriter getOut() {
		return out;
	}

	public RequestHandlerBinding(Request r, GroovyRequestHandler h, PrintWriter out) {
		request = r;
		handler = h;
		this.out = out;
		handled = false;
	}
	public RequestHandlerBinding(Request r, GroovyRequestHandler h) {
		out = new PrintWriter(System.out);
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
		} else if(name.equals("getpriority")) {
			return false;
		} else {
			return super.getVariable(name);
		}
	}
}
