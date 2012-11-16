package slightlysain.jvtftp.server;

import java.util.concurrent.ExecutorService;

import slighltysain.jvtftp.request.router.RequestRouter;
import junit.framework.TestCase;

public class TestServerRequestHandler extends TestCase {

	ServerRequestHandler requestHandler;
	RequestRouter requestRouter;
	ExecutorService executorService;
	ClientRegister clientRegister;

	public void testRequestHandlerConstuctor() {
		requestHandler = new ServerRequestHandler(null, clientRegister,
				executorService, requestRouter);

	}

}
