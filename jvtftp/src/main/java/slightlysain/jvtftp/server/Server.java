package slightlysain.jvtftp.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.NotRequestPacketException;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.RequestFactory;
import slightlysain.jvtftp.request.router.RequestRouter;
import slightlysain.jvtftp.tftpadapter.OnPacketHandler;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;

/**
 * Responsible for starting a UDP socket listening on selected port in the
 * configuration file. When a connection is received a ServerRequestHandler is
 * created the connection allocated to it.
 * 
 * @author slightlysain
 * 
 */
public class Server {

	private TFTPAdapter packetAdapter;
	private RequestRouter requestRouter;
	private ClientRegister clientRegister;
	private ExecutorService executor;
	private int port;

	private Logger log = LoggerFactory.getLogger(Server.class);

	/**
	 * nested class to handle server on packet events this class will check if
	 * the packet is an error packet. If the packet is not an error packet a new
	 * ServerRequestHandler and Request is created. The request is passed to the
	 * ServerRequestHandler. If the packet passed to the RequestFactory is not a
	 * TFTPRequestPacket an exception is thrown. The exception is logged and the
	 * server becomes ready to handle the next packet.
	 * 
	 * @author slightlysain
	 * 
	 */
	private final class ServerOnPacketHandler implements OnPacketHandler {
		public void onPacket(TFTPPacket packet) {
			boolean error = TFTPPacket.ERROR == packet.getType();
			if (error) {
				new ServerErrorHandler((TFTPErrorPacket) packet);
			} else {
				try {
					Request req = RequestFactory.newRequest(packet);
					new ServerRequestHandler(req, clientRegister, executor,
							requestRouter);
				} catch (NotRequestPacketException e) {
					log.error(
							"The request recieved is not a request or error packet",
							e);
				} catch (ClientAlreadyRegisteredException e) {
					log.error("the client is already registered with the ClientRegister");
				}
			}
		}
	}

	public Server(int port, RequestRouter aNewRequestRouter,
			ExecutorService executor, TFTPAdapter adapter,
			ClientRegister clientRegister) {
		this.requestRouter = aNewRequestRouter;
		this.port = port;
		this.executor = executor;
		this.packetAdapter = adapter;
		this.clientRegister = clientRegister;
	}

	public void start() throws SocketException, IOException,
			TFTPPacketException {
		OnPacketHandler packetHandler = new ServerOnPacketHandler();
		packetAdapter.setPacketHandler(packetHandler);
		packetAdapter.open(port);
		packetAdapter.listen();
	}
}
