package slightlysain.jvtftp.request.chain;

import java.util.List;
import java.util.Set;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.RequestHandler;

public interface RequestHandlerChain {
	
	public abstract void set(Set<RequestHandler> handlerSet);
	public abstract void setNextChain(RequestHandlerChain handlerChain);
	public abstract void execute(Request request);
	
}
