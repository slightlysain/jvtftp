package slightlysain.jvtftp.request.handler;

import slightlysain.jvtftp.request.Request;

public abstract class AbstractRequestHandler implements RequestHandler {
	private RequestHandler next;
	
	public void execute(Request request) {
		next.execute(request);
	}

	public void setNext(RequestHandler requestHandler) {
		next = requestHandler;
	}

	public void skip(Request request) {
		next.execute(request);
	}	
}
