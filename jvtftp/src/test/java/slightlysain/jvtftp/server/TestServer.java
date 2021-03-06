package slightlysain.jvtftp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.apache.commons.net.tftp.TFTPReadRequestPacket;
import org.apache.commons.net.tftp.TFTPWriteRequestPacket;
import org.jmock.Expectations;
import org.jmock.Mockery;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.router.RequestRouter;
import slightlysain.jvtftp.tftpadapter.OnPacketHandler;
import slightlysain.jvtftp.tftpadapter.OnTimeoutHandler;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import slightlysain.mock.CatchInstanceAction;

import junit.framework.TestCase;

public class TestServer extends TestCase {

	Mockery context;

	Server server;

	RequestRouter requestRouter;
	ExecutorService executorService;
	TFTPAdapter adapter;
	ClientRegister clientRegister;
	OnPacketHandler packetHandler;
	OnTimeoutHandler timeoutHandler;
	int port;

	private void createNewServer() {
		context = new Mockery();
		requestRouter = (RequestRouter) context.mock(RequestRouter.class);
		executorService = (ExecutorService) context.mock(ExecutorService.class);
		adapter = context.mock(TFTPAdapter.class);
		clientRegister = (ClientRegister) context.mock(ClientRegister.class);
		server = new Server(port, requestRouter, executorService, adapter,
				clientRegister);
	}

	private void startServer(Expectations expectations) {
		context.checking(expectations);
		try {
			server.start();
		} catch (IOException e) {
			fail("IOException");
		} catch (TFTPPacketException e) {
			fail("TFTP packet exception");
		}
	}

	public void testServerConstructor() {
		createNewServer();
		context.assertIsSatisfied();
	}

	public void testStartServer() throws IOException, TFTPPacketException {
		createNewServer();
		startServer(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(adapter).open(port);
				oneOf(adapter).listen();
			}
		});
		context.assertIsSatisfied();
	}

	public void testReadRequestPacket() throws IOException,
			TFTPPacketException, ClientAlreadyRegisteredException {
		final CatchInstanceAction<OnPacketHandler> packetHandler = new CatchInstanceAction<OnPacketHandler>();
		createNewServer();
		startServer(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				will(packetHandler);
				oneOf(adapter).open(port);
				oneOf(adapter).listen();
			}
		});

		final InetAddress host = InetAddress.getByName("10.10.10.1");
		TFTPReadRequestPacket p = new TFTPReadRequestPacket(host, 2500, "test",
				TFTP.ASCII_MODE);
		context.checking(new Expectations() {
			{
				oneOf(clientRegister).contains(with(any(Request.class)));
				will(returnValue(false));
				oneOf(clientRegister).register(with(any(Request.class)));
				oneOf(executorService).execute(
						with(any(ServerRequestHandler.class)));
			}
		});
		// create a fake packet and call onPacketHandler

		packetHandler.getInstance().onPacket(p);

		context.assertIsSatisfied();
	}

	public void testWriteRequestPacket() throws IOException,
			TFTPPacketException, ClientAlreadyRegisteredException {
		final CatchInstanceAction<OnPacketHandler> packetHandler = new CatchInstanceAction<OnPacketHandler>();
		createNewServer();
		startServer(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				will(packetHandler);
				oneOf(adapter).open(port);
				oneOf(adapter).listen();
			}
		});

		final InetAddress host = InetAddress.getByName("10.10.10.1");
		TFTPWriteRequestPacket p = new TFTPWriteRequestPacket(host, 2500,
				"test", TFTP.ASCII_MODE);
		context.checking(new Expectations() {
			{
				oneOf(clientRegister).contains(with(any(Request.class)));
				will(returnValue(false));
				oneOf(clientRegister).register(with(any(Request.class)));
				oneOf(executorService).execute(
						with(any(ServerRequestHandler.class)));
			}
		});
		// create a fake packet and call onPacketHandler

		packetHandler.getInstance().onPacket(p);

		context.assertIsSatisfied();
	}

	public void testRegisteredWriteRequestPacket() throws IOException,
			TFTPPacketException {
		final CatchInstanceAction<OnPacketHandler> packetHandler = new CatchInstanceAction<OnPacketHandler>();
		createNewServer();
		startServer(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				will(packetHandler);
				oneOf(adapter).open(port);
				oneOf(adapter).listen();
			}
		});

		final InetAddress host = InetAddress.getByName("10.10.10.1");
		TFTPWriteRequestPacket p = new TFTPWriteRequestPacket(host, 2500,
				"test", TFTP.ASCII_MODE);
		context.checking(new Expectations() {
			{
				oneOf(clientRegister).contains(with(any(Request.class)));
				will(returnValue(true));
			}
		});
		// create a fake packet and call onPacketHandler

		packetHandler.getInstance().onPacket(p);

		context.assertIsSatisfied();
	}

	public void testRegisteredReadRequestPacket() throws IOException,
			TFTPPacketException {
		final CatchInstanceAction<OnPacketHandler> packetHandler = new CatchInstanceAction<OnPacketHandler>();
		createNewServer();
		startServer(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				will(packetHandler);
				oneOf(adapter).open(port);
				oneOf(adapter).listen();
			}
		});

		final InetAddress host = InetAddress.getByName("10.10.10.1");
		TFTPReadRequestPacket p = new TFTPReadRequestPacket(host, 2500, "test",
				TFTP.ASCII_MODE);
		context.checking(new Expectations() {
			{
				oneOf(clientRegister).contains(with(any(Request.class)));
				will(returnValue(true));
			}
		});
		packetHandler.getInstance().onPacket(p);
		context.assertIsSatisfied();
	}

	public void testErrorPacket() throws IOException, TFTPPacketException {
		final CatchInstanceAction<OnPacketHandler> packetHandler = new CatchInstanceAction<OnPacketHandler>();
		createNewServer();
		startServer(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				will(packetHandler);
				oneOf(adapter).open(port);
				oneOf(adapter).listen();
			}
		});

		final InetAddress host = InetAddress.getByName("10.10.10.1");
		TFTPErrorPacket p = new TFTPErrorPacket(host, port, 0, "Some error");

		packetHandler.getInstance().onPacket(p);

		context.assertIsSatisfied();
	}

	public void testOpenSocketException() throws IOException,
			TFTPPacketException {
		boolean ex = false;
		createNewServer();
		context.checking(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(adapter).open(port);
				will(throwException(new SocketException()));
			}
		});

		try {
			server.start();
		} catch (IOException e) {
			ex = true;
		} catch (TFTPPacketException e) {
			ex = true;
		}
		context.assertIsSatisfied();
		assertTrue(ex);
	}

	public void testListenException() throws IOException, TFTPPacketException {
		boolean ex = false;
		createNewServer();
		context.checking(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(adapter).open(port);
				oneOf(adapter).listen();
				will(throwException(new IOException()));
			}
		});

		try {
			server.start();
		} catch (IOException e) {
			ex = true;
		} catch (TFTPPacketException e) {
			ex = true;
		}
		context.assertIsSatisfied();
		assertTrue(ex);
	}
}
