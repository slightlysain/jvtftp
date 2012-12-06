package slightlysain.jvtftp.session;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.event.ListSelectionEvent;

import org.apache.commons.net.tftp.TFTPDataPacket;
import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.tftpadapter.OnPacketHandler;
import slightlysain.jvtftp.tftpadapter.OnTimeoutHandler;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import slightlysain.mock.MockDataPacket;
import slightlysain.mock.MockErrorPacket;
import slightlysain.mock.MockErrorPacketGenerator;
import slightlysain.mock.MockPacketGenerator;

public class TestRecieveSession {

	RecieveSession session;
	Mockery context;
	PacketAdapterFactory factory;
	TFTPAdapter tftpadapter;
	InetAddress host;
	int port;
	ByteArrayOutputStream output;

	@Before
	public void startup() throws UnknownHostException {
		context = new Mockery();
		host = InetAddress.getByName("10.25.25.1");
		port = 5000;
		output = new ByteArrayOutputStream();
		factory = context.mock(PacketAdapterFactory.class);
		tftpadapter = context.mock(TFTPAdapter.class);
	}

	@Test
	public void test_ErrorResponseOnStartup() throws IOException {
		final int error = TFTPErrorPacket.UNDEFINED;
		final String message = "error on first packet";
		final MockErrorPacket errorpacket = new MockErrorPacket(host, port,
				error, message);
	
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new RecieveSession(factory, host, port, tftpadapter, output);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendAckPacket(host, port, 0);
				will(new MockErrorPacketGenerator(session, host, port, error,
						message));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(errorpacket));
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
	}

	@Test
	public void test_ErrorResponseOnSecondPacket() throws IOException {
		final int error = TFTPErrorPacket.UNDEFINED;
		final String message = "error on second packet";
		byte[] data = new byte[512];
		new Random().nextBytes(data);
		final TFTPPacket tftppacket = new TFTPDataPacket(host, port, 1, data);
		final MockDataPacket datapacket = new MockDataPacket(host, port, data,
				1);
		final MockErrorPacket errorpacket = new MockErrorPacket(host, port,
				error, message);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new RecieveSession(factory, host, port, tftpadapter, output);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendAckPacket(host, port, 0);
				will(new MockPacketGenerator(session, tftppacket));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(datapacket));
				oneOf(tftpadapter).sendAckPacket(host, port, 1);
				will(new MockErrorPacketGenerator(session, host, port, error,
						message));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(errorpacket));
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
		// byte[] out = output.toByteArray();
		// System.out.println("data.len=" + data.length + "output.len="
		// + out.length);
		// for (int i = 0; i < data.length; i++) {
		// System.out.println("data[" + i +"]=" + data[i] + "out[" + i + "]=" +
		// out[i]);
		// }
		assertArrayEquals(data, output.toByteArray());
	}

	@Test
	public void test_OnePacket() throws IOException {
		byte[] data = new byte[500];
		new Random().nextBytes(data);
		final TFTPPacket tftppacket = new TFTPDataPacket(host, port, 1, data);
		final MockDataPacket datapacket = new MockDataPacket(host, port, data,
				1);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new RecieveSession(factory, host, port, tftpadapter, output);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendAckPacket(host, port, 0);
				will(new MockPacketGenerator(session, tftppacket));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(datapacket));
				oneOf(tftpadapter).sendAckPacket(host, port, 1);
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
		assertArrayEquals(data, output.toByteArray());
	}

	@Test
	public void test_TwoPacket() throws IOException {
		byte[] data1 = new byte[512];
		byte[] data2 = new byte[500];
		new Random().nextBytes(data1);
		new Random().nextBytes(data2);
		final TFTPPacket tftppacket1 = new TFTPDataPacket(host, port, 1, data1);
		final MockDataPacket datapacket1 = new MockDataPacket(host, port,
				data1, 1);
		final TFTPPacket tftppacket2 = new TFTPDataPacket(host, port, 2, data2);
		final MockDataPacket datapacket2 = new MockDataPacket(host, port,
				data2, 2);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new RecieveSession(factory, host, port, tftpadapter, output);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendAckPacket(host, port, 0);
				will(new MockPacketGenerator(session, tftppacket1));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(datapacket1));
				oneOf(tftpadapter).sendAckPacket(host, port, 1);
				will(new MockPacketGenerator(session, tftppacket2));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(datapacket2));
				oneOf(tftpadapter).sendAckPacket(host, port, 2);
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
		byte[] sentdata = new byte[data1.length + data2.length];
		System.arraycopy(data1, 0, sentdata, 0, data1.length);
		System.arraycopy(data2, 0, sentdata, data1.length,data2.length);
		assertArrayEquals(sentdata, output.toByteArray());
		
	}

}
