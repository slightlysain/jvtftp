package slightlysain.jvtftp.request.chain;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.RequestHandler;
import slightlysain.jvtftp.request.router.RequestHandlerPriority;

public class RequestHandlerChainImpl implements RequestHandlerChain {
	private Set<RequestHandler> handlerSet;
	private RequestHandlerChain nextChain = new NullRequestHandlerChain();
	private RequestHandlerPriority priority;
	private Logger log = LoggerFactory.getLogger(getClass());

	public void execute(Request request) {
		if (0 == handlerSet.size()) {
			nextChain.execute(request);
		} else {
			RequestHandler[] hands = handlerSet.toArray(new RequestHandler[0]);
			final int lastElement = (hands.length - 1);
			for (int i = 0; i < hands.length; i++) {
				if (lastElement != i) {
					hands[i].setNext(hands[i + 1]);
				} else {
					hands[i].setNext(new NextChainRequestHandler(nextChain));
				}
			}
			hands[0].execute(request);
		}
	}

	public void set(Set<RequestHandler> handlerSet) {
		this.handlerSet = handlerSet;
	}

	public void setNextChain(RequestHandlerChain handlerChain) {
		nextChain = handlerChain;
	}

}
