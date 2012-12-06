package slightlysain.jvtftp.session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.net.tftp.TFTPPacketException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Ignore;
import org.junit.Test;

import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.tftpadapter.OnPacketHandler;
import slightlysain.jvtftp.tftpadapter.OnTimeoutHandler;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;

public class TestErrorSession {

	Mockery context = new Mockery();
	TFTPAdapter adapter;
	InetAddress host;

	@Test
	public void test_newErrorSession() throws IOException, TFTPPacketException {
		final int errorcode = 1;
		final String message = "test error";
		PacketAdapterFactory packetFactory = context
				.mock(PacketAdapterFactory.class);
		adapter = context.mock(TFTPAdapter.class);
		final int port = 5000;
		try {
			host = InetAddress.getByName("10.25.4.5");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.checking(new Expectations() {
			{
				oneOf(adapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(adapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));

				oneOf(adapter).sendErrorPacket(host, port, errorcode, message);
				oneOf(adapter).close();
			}
		});

		ErrorSession session = new ErrorSession(packetFactory, host, port,
				adapter, errorcode, message);
		session.start();
	}

}
