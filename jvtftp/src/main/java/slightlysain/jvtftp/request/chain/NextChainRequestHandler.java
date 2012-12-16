package slightlysain.jvtftp.request.chain;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.NoPriorityException;
import slightlysain.jvtftp.request.handler.RequestHandler;
import slightlysain.jvtftp.request.handler.RequestHandlerPriority;

final class NextChainRequestHandler implements RequestHandler {
	private RequestHandlerChain nextChain;

	NextChainRequestHandler(RequestHandlerChain nextChain) {
		this.nextChain = nextChain;
	}

	public void setNext(RequestHandler requestHandler) {
		//do nothing
	}

	public void execute(Request request) {
		nextChain.execute(request);
	}

	public RequestHandlerPriority getPriority() throws NoPriorityException {
		//do nothing
		return null;
	}

	public void skip(Request request) {
		//do nothing
	}
}