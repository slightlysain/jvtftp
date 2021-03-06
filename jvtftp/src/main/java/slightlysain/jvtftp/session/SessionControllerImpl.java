package slightlysain.jvtftp.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.commons.net.io.FromNetASCIIOutputStream;
import org.apache.commons.net.io.ToNetASCIIInputStream;
import org.apache.commons.net.tftp.TFTPPacketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.io.JvtftpFromNetASCIIOutputStream;
import slightlysain.jvtftp.io.JvtftpToNetASCIIInputStream;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactoryImpl;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import slightlysain.jvtftp.tftpadapter.TFTPAdapterFactory;

public class SessionControllerImpl implements SessionController {
	private TFTPAdapter adapter;
	private Request request;
	private InetAddress clientAddress;
	private int port;
	private SessionFactory factory;// = new PacketAdapterFactoryImpl();
	private Logger log = LoggerFactory.getLogger(getClass());

	public SessionControllerImpl(Request request,
			TFTPAdapterFactory adapterFactory, SessionFactory factory) {
		this.clientAddress = request.getAddress();
		this.port = request.getPort();
		this.adapter = adapterFactory.createTFTPAdapter();
		this.factory = factory;
		this.request = request;
	}

	public void error(int errorid, String message) {
		Session session = factory.createErrorSession(adapter, clientAddress,
				port, errorid, message);
		try {
			adapter.open(0);
			session.start();
		} catch (SocketException e) {
			log.error("Could not send error SocketException");
		} catch (IOException e) {
			log.error("Could not send error IOException");
		}
	}

	public void send(InputStream in) {
		InputStream chunkerIn;
		if (request.isNetASCII()) {
			chunkerIn = new JvtftpToNetASCIIInputStream(in);
		} else {
			chunkerIn = in;
		}
		Chunker chunker = factory.createChunker(chunkerIn);
		Session session = factory.createSendSession(adapter, clientAddress,
				port, chunker);
		try {
			adapter.open(0);
			session.start();
			adapter.listen();
		} catch (IOException e) {
			log.error("IOException", e);
		} catch (TFTPPacketException e) {
			log.error("TFTPPacketException", e);
		}
	}

	public void recieve(OutputStream out) {
		if (request.isNetASCII()) {
			out = new JvtftpFromNetASCIIOutputStream(out);
		}
		Session session = factory.createRecieveSession(adapter, clientAddress,
				port, out);
		try {
			adapter.open(0);
			session.start();
			adapter.listen();
		} catch (IOException e) {
			log.error("Cannot start recieve IOException", e);
		} catch (TFTPPacketException e) {
			log.error("TFTPPacketException", e);
		}
	}

}
