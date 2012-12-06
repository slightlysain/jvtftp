package slightlysain.jvtftp.request.chain;

import java.util.List;
import java.util.Set;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.RequestHandler;

public class NullRequestHandlerChain implements RequestHandlerChain {

	public void execute(Request request) {
		//do nothing
	}

	public void set(Set<RequestHandler> handlerSet) {
		// TODO Auto-generated method stub
		
	}

	public void setNextChain(RequestHandlerChain handlerChain) {
		// TODO Auto-generated method stub
		
	}

}
