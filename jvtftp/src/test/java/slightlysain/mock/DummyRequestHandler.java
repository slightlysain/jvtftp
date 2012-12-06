package slightlysain.mock;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.NoPriorityException;
import slightlysain.jvtftp.request.handler.RequestHandler;
import slightlysain.jvtftp.request.router.RequestHandlerPriority;

public class DummyRequestHandler implements RequestHandler {

	public RequestHandlerPriority getPriority() throws NoPriorityException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setNext(RequestHandler requestHandler) {
		// TODO Auto-generated method stub

	}

	public void execute(Request request) {
		// TODO Auto-generated method stub

	}

	public void skip(Request request) {
		// TODO Auto-generated method stub
		
	}

}
