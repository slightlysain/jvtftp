package slightlysain.jvtftp.session;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;

public class SessionFactoryImpl implements SessionFactory {
	private PacketAdapterFactory packetFactory;

	public SessionFactoryImpl(PacketAdapterFactory packetFactory) {
		if(null == packetFactory) { 
			throw new NullPointerException("null packet factory");
		}
		this.packetFactory = packetFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slightlysain.jvtftp.session.SessionFactory#createErrorSession(slightlysain
	 * .jvtftp.packetadapter.PacketAdapterFactory, java.net.InetAddress, int,
	 * slightlysain.jvtftp.tftpadapter.TFTPAdapter)
	 */
	public Session createErrorSession(InetAddress clientAddress, int port,
			TFTPAdapter tftpadapter, int code, String message) {
		return new ErrorSession(packetFactory, clientAddress, port,
				tftpadapter, code, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slightlysain.jvtftp.session.SessionFactory#createChunker(java.io.InputStream
	 * )
	 */
	public Chunker createChunker(InputStream input) {
		return new ChunkerImpl(input);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slightlysain.jvtftp.session.SessionFactory#createSendSession(slightlysain
	 * .jvtftp.tftpadapter.TFTPAdapter, java.net.InetAddress, int,
	 * slightlysain.jvtftp.session.Chunker,
	 * slightlysain.jvtftp.packetadapter.PacketAdapterFactory)
	 */
	public Session createSendSession(TFTPAdapter tftpadapter,
			InetAddress clientAddress, int port, Chunker chunker) {
		return new SendSession(tftpadapter, clientAddress, port, chunker,
				packetFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slightlysain.jvtftp.session.SessionFactory#createRecieveSession(slightlysain
	 * .jvtftp.packetadapter.PacketAdapterFactory, java.net.InetAddress, int,
	 * slightlysain.jvtftp.tftpadapter.TFTPAdapter, java.io.OutputStream)
	 */
	public Session createRecieveSession(InetAddress clientAddress, int port,
			TFTPAdapter tftpadapter, OutputStream output) {
		return new RecieveSession(packetFactory, clientAddress, port,
				tftpadapter, output);
	}

	public SessionController createController(Request request,
			TFTPAdapter tftpadapter) {
		SessionController controller = new SessionControllerImpl(request,
				tftpadapter, this);
		return controller;
	}

}
