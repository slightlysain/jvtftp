package slightlysain.mock;

import java.net.InetAddress;

import org.apache.commons.net.tftp.TFTPDataPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

import slightlysain.jvtftp.session.Session;

public class MockPacketGenerator implements Action {
	Session session;
	TFTPPacket packet;
	
	public MockPacketGenerator(Session session, TFTPPacket packet) {
		this.session = session;
		this.packet = packet;
		
	}
	
	public void describeTo(Description description) {
		description.appendText("mock packet generator");
	}

	public Object invoke(Invocation invocation) throws Throwable {
		session.onPacket(packet);
		return null;
	}

}
