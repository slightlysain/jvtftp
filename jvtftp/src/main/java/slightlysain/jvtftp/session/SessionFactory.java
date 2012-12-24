package slightlysain.jvtftp.session;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;

public interface SessionFactory {

	public abstract Session createErrorSession(TFTPAdapter adapter, InetAddress clientAddress,
			int port, int code, String message);

	public abstract Chunker createChunker(InputStream input);

	public abstract Session createSendSession(TFTPAdapter adapter, InetAddress clientAddress,
			int port, Chunker chunker);

	public abstract Session createRecieveSession(TFTPAdapter adapter,InetAddress clientAddress,
			int port, OutputStream output);

	public abstract SessionController createController(Request request);

}