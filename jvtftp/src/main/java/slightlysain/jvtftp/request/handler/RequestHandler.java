package slightlysain.jvtftp.request.handler;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.router.RequestHandlerPriority;

public interface RequestHandler {
	public abstract RequestHandlerPriority getPriority() throws NoPriorityException;
	public abstract void setNext(RequestHandler requestHandler);
	public abstract void execute(Request request);
	public abstract void skip(Request request);
}
