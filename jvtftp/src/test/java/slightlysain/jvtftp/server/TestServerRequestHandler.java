package slightlysain.jvtftp.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.UnknownRequestTypeException;
import slightlysain.jvtftp.request.router.RequestRouter;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import slightlysain.mock.CatchInstanceAction;
import junit.framework.TestCase;

public class TestServerRequestHandler {

	Mockery context;
	ServerRequestHandler requestHandler;
	RequestRouter requestRouter;
	ExecutorService executorService;
	ClientRegister clientRegister;
	Request req;

	private void createMocks() {
		context = new Mockery();
		requestRouter = (RequestRouter) context.mock(RequestRouter.class);
		executorService = (ExecutorService) context.mock(ExecutorService.class);
		clientRegister = (ClientRegister) context.mock(ClientRegister.class);
		req = (Request) context.mock(Request.class);

	}

	@Test
	public void testClientRegisterEmpty()
			throws ClientAlreadyRegisteredException {
		createMocks();
		context.checking(new Expectations() {
			{
				oneOf(clientRegister).contains(req);
				will(returnValue(false));
				oneOf(clientRegister).register(req);
				oneOf(executorService).execute(with(any(Runnable.class)));
			}
		});

		requestHandler = new ServerRequestHandler(req, clientRegister,
				executorService, requestRouter);
		context.assertIsSatisfied();
	}

	@Test
	public void testClientRegisterHasRequest() throws UnknownHostException,
			ClientAlreadyRegisteredException {
		createMocks();
		final InetAddress host = InetAddress.getByName("10.10.10.10");
		context.checking(new Expectations() {
			{
				oneOf(clientRegister).contains(req);
				will(returnValue(true));
				never(clientRegister).register(req);
				never(clientRegister).contains(with(any(InetAddress.class)),
						with(any(Integer.class)));
				allowing(req).getAddress();
				will(returnValue(host));
				allowing(req).getFilename();
				will(returnValue("test.txt"));
				allowing(req).getPort();
				will(returnValue(2050));
				allowing(req).getTime();
				will(returnValue(1000));
			}
		});

		requestHandler = new ServerRequestHandler(req, clientRegister,
				executorService, requestRouter);
		context.assertIsSatisfied();
	}

	@Test
	public void testRunMethod() throws UnknownHostException,
			UnknownRequestTypeException, ClientAlreadyRegisteredException, ClientNotRegisteredException {
		createMocks();
		final CatchInstanceAction<Runnable> runCatcher = new CatchInstanceAction<Runnable>();
		final InetAddress host = InetAddress.getByName("10.10.10.10");
		context.checking(new Expectations() {
			{
				oneOf(clientRegister).contains(req);
				will(returnValue(false));
				oneOf(clientRegister).register(req);
				oneOf(executorService).execute(with(any(Runnable.class)));
				will(runCatcher);
				never(clientRegister).contains(with(any(InetAddress.class)),
						with(any(Integer.class)));
				allowing(req).getAddress();
				will(returnValue(host));
				allowing(req).getFilename();
				will(returnValue("test.txt"));
				allowing(req).getPort();
				will(returnValue(2050));
				allowing(req).getTime();
				will(returnValue(1000));
			}
		});

		requestHandler = new ServerRequestHandler(req, clientRegister,
				executorService, requestRouter);

		context.checking(new Expectations() {
			{
				oneOf(requestRouter).makeRequest(req);
				oneOf(clientRegister).unregister(req);
			}
		});

		runCatcher.getInstance().run();

		context.assertIsSatisfied();

	}

}
