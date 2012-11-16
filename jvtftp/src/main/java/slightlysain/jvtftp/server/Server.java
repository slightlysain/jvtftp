package slightlysain.jvtftp.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.net.tftp.TFTPPacket;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slighltysain.jvtftp.request.router.RequestRouter;
import slightlysain.jvtftp.tftpadapter.OnPacketHandler;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;

/**
 * Responsible for starting a udp socket listening on selected port in the
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

	public Server(int port, RequestRouter aNewRequestRouter,
			ExecutorService executor, TFTPAdapter adapter,
			ClientRegister clientRegister) {
		this.requestRouter = aNewRequestRouter;
		this.port = port;
		this.executor = executor;
		this.packetAdapter = adapter;
	}

	public void start() throws SocketException, IOException,
			TFTPPacketException {
		OnPacketHandler packetHandler = new OnPacketHandler() {
			public void onPacket(TFTPPacket packet) {
				new ServerRequestHandler(packet, clientRegister, executor,
						requestRouter);
			}
		};
		packetAdapter.setPacketHandler(packetHandler);
		packetAdapter.open(port);
		packetAdapter.listen();
	}
}
