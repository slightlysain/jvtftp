package slightlysain.mock;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPAckPacket;
import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

import slightlysain.jvtftp.session.Chunker;
import slightlysain.jvtftp.session.Session;

public class MockSessionAck implements Action {
	private MockChunker chunker;
	private InetAddress client;
	private int port;
	private Session session;
	
	
	public MockSessionAck(Session session, MockChunker chunker, InetAddress client, int port) {
		this.chunker = chunker;
		this.client = client;
		this.port = port;
		this.session = session;
	}

	public void describeTo(Description description) {
		description.appendText("mock session ack").appendValue(chunker.getLastBlock() + 1);
	}

	public Object invoke(Invocation invocation) throws Throwable {
		TFTPAckPacket packet = new TFTPAckPacket(client, port, chunker.getLastBlock() + 1);
		session.onPacket(packet);
		return null;
	}

}
