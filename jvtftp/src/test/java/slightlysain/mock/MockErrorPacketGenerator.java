package slightlysain.mock;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

import slightlysain.jvtftp.session.Session;

public class MockErrorPacketGenerator implements Action {
	Session session;
	InetAddress host;
	int port;
	int error;
	String message;

	public MockErrorPacketGenerator(Session session, InetAddress host,
			int port, int error, String message) {
		this.session = session;
		this.host = host;
		this.port = port;
		this.message = message;
		this.error = error;
	}

	public void describeTo(Description description) {
		description.appendText("MockErrorPacketGenerator");
	}

	public Object invoke(Invocation invocation) throws Throwable {
		TFTPErrorPacket packet = new TFTPErrorPacket(host, port, error, message);
		session.onPacket(packet);
		return null;
	}

}
