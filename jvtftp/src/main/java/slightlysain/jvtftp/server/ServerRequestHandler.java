package slightlysain.jvtftp.server;

import java.util.concurrent.ExecutorService;

import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slighltysain.jvtftp.request.Request;
import slighltysain.jvtftp.request.RequestImpl;
import slighltysain.jvtftp.request.UnknownRequestTypeException;
import slighltysain.jvtftp.request.router.RequestRouter;
import slightlysain.jvtftp.session.Session;
import slightlysain.jvtftp.session.SessionImpl;

/**
 * responsible for the handling of requests. A thread will be allocated to the
 * request if a session does not already exist for the current request.
 * 
 * @author slightlysain
 * 
 */
public class ServerRequestHandler implements Runnable {

	private TFTPPacket initPacket;
	private TFTPRequestPacket requestPacket;
	private ClientRegister clientRegister;
	private ExecutorService executorService;
	private RequestRouter requestRouter;
	private Session session;
	private boolean abort = false;

	Logger log = LoggerFactory.getLogger(this.getClass());

	public ServerRequestHandler(TFTPPacket packet,
			ClientRegister aNewClientsRegister,
			ExecutorService aNewExecutorService, RequestRouter aNewRequestRouter) {
		this.initPacket = packet;
		this.clientRegister = aNewClientsRegister;
		this.executorService = aNewExecutorService;
		this.requestRouter = aNewRequestRouter;
		log.debug("new server request handler created");
		if (isRequestPacket()) {
			requestPacket = (TFTPRequestPacket) initPacket;
		} else {
			log.error("packet is not a requestPacket" + initPacket.getAddress()
					+ ":" + initPacket.getPort());
			abort = true;
			return;
		}
		if (sessionAlreadyExists()) {
			log.debug("Session already exists for:"
					+ requestPacket.getAddress() + ":"
					+ requestPacket.getPort());
			return;
		}
		executorService.execute(this);
	}

	public void run() {

		if (abort) {
			log.error("request aborted");
			return;
		}
		log.trace("Running new instance of ServerRequestHandler for:"
				+ initPacket.getAddress() + ":" + initPacket.getPort());
		if (sessionAlreadyExists()) {
			log.debug("Session already exists for:"
					+ requestPacket.getAddress() + ":"
					+ requestPacket.getPort());
			return;
		} else {
			log.debug("handling request");
			handleServerRequest();
		}
		log.trace("ending server request handler");
	}

	private boolean sessionAlreadyExists() {
		return clientRegister.contains(requestPacket.getAddress(),
				requestPacket.getPort());
	}

	private boolean isRequestPacket() {
		int packetType = initPacket.getType();
		return (TFTPPacket.READ_REQUEST == packetType || TFTPPacket.WRITE_REQUEST == packetType);
	}

	private void handleServerRequest() {
		if (!addSession()) {
			log.debug("Could not register client :"
					+ requestPacket.getAddress() + ":"
					+ requestPacket.getPort());
			return;
		}
		try {
			routeRequest();
		} catch (UnknownRequestTypeException e) {
			log.error("Request type is not known", e);
		} finally {
			removeSession();
		}
	}

	private void removeSession() {
		log.debug("removed session :" + requestPacket.getAddress() + ":"
				+ requestPacket.getPort());
		clientRegister.unregister(requestPacket.getAddress(),
				requestPacket.getPort());
	}

	private boolean addSession() {
		log.debug("added session to register:" + requestPacket.getAddress()
				+ ":" + requestPacket.getPort());
		return clientRegister.register(requestPacket.getAddress(),
				requestPacket.getPort());
	}

	private void routeRequest() throws UnknownRequestTypeException {
		Request request = new RequestImpl(requestPacket);
		session = new SessionImpl(requestPacket.getAddress(),
				requestPacket.getPort(), request);
		executorService.execute((Runnable) session);
		requestRouter.makeRequest(request);
		request.waitForClosedSession();
	}
}
