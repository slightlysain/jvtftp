package slightlysain.jvtftp.server;

import java.util.concurrent.ExecutorService;

import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPRequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.AbstractRequest;
import slightlysain.jvtftp.request.UnknownRequestTypeException;
import slightlysain.jvtftp.request.router.RequestRouter;
import slightlysain.jvtftp.session.SessionController;
import slightlysain.jvtftp.session.SessionControllerImpl;

/**
 * responsible for the handling of requests. A thread will be allocated to the
 * request if a session does not already exist for the current request.
 * 
 * @author slightlysain
 * 
 */
public class ServerRequestHandler implements Runnable {

	private Request request;
	private ClientRegister clientRegister;
	private ExecutorService executorService;
	private RequestRouter requestRouter;

	Logger log = LoggerFactory.getLogger(getClass());

	public ServerRequestHandler(Request request,
			ClientRegister aNewClientsRegister,
			ExecutorService aNewExecutorService, RequestRouter aNewRequestRouter)
			throws ClientAlreadyRegisteredException {
		this.clientRegister = aNewClientsRegister;
		this.executorService = aNewExecutorService;
		this.requestRouter = aNewRequestRouter;
		this.request = request;
		if (clientRegister.contains(request)) {
			log.info("client " + request.getAddress().getHostAddress()
					+ "'s request for file:" + request.getFilename()
					+ " is already being handled");
		} else {
			clientRegister.register(request);
			executorService.execute(this);
		}
	}

	public void run() {
		handleServerRequest();
	}

	private void handleServerRequest() {
		try {
			requestRouter.makeRequest(request);
		} catch (UnknownRequestTypeException e) {
			log.error("Request type is not known", e);
		} catch (Exception e) {
			log.error("exception caught when making request", e);
		}
		try {
			clientRegister.unregister(request);
		} catch (ClientNotRegisteredException e) {
			log.error("Client is not currently registered!", e);
		}
	}

	// private void routeRequest() throws UnknownRequestTypeException {
	//
	// // session = new SessionImpl(requestPacket.getAddress(),
	// // requestPacket.getPort(), request);
	// // executorService.execute((Runnable) session);
	// // requestRouter.makeRequest(request);
	// }
}
