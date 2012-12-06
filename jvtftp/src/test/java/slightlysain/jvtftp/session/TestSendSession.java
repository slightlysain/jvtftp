package slightlysain.jvtftp.session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import slightlysain.jvtftp.packetadapter.AckPacket;
import slightlysain.jvtftp.packetadapter.AckPacketImpl;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.tftpadapter.OnPacketHandler;
import slightlysain.jvtftp.tftpadapter.OnTimeoutHandler;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import slightlysain.mock.MockAckPacket;
import slightlysain.mock.MockChunker;
import slightlysain.mock.MockErrorPacket;
import slightlysain.mock.MockErrorPacketGenerator;
import slightlysain.mock.MockSessionAck;

public class TestSendSession {
	InetAddress host;
	TFTPAdapter tftpadapter;
	int port = 5000;
	MockChunker chunker;
	PacketAdapterFactory factory;
	Mockery context;
	byte[] bytes;
	Session session;

	@Before
	public void setup() throws UnknownHostException {
		context = new Mockery();
		host = InetAddress.getByName("10.25.4.4");
	}

	@Test
	public void test_createNoByteSession() throws IOException {
		bytes(0);
	}

	@Test
	public void test_create512ByteSession() throws IOException {
		bytes(512);
	}

	@Test
	public void test_create510ByteSession() throws IOException {
		bytes(510);
	}

	@Test
	public void test_create511ByteSession() throws IOException {
		bytes(511);
	}

	@Test
	public void test_create513ByteSession() throws IOException {
		bytes(513);
	}

	@Test
	public void test_create1024ByteSession() throws IOException {
		bytes(1024);
	}

	@Test
	public void test_create1025ByteSession() throws IOException {
		bytes(1025);
	}

	@Test
	public void test_create100KByteSession() throws IOException {
		bytes(100000);
	}

	@Test
	public void test_createLessThan512ByteStream() throws IOException {
		bytes(500);
	}

	@Test(expected = IOException.class)
	public void test_SendIOException_1Block() throws IOException {
		tftpadapter = context.mock(TFTPAdapter.class);
		host = InetAddress.getByName("10.25.4.4");
		factory = context.mock(PacketAdapterFactory.class);
		chunker = new MockChunker(1);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new SendSession(tftpadapter, host, port, chunker, factory);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendDataPacket(with(equal(host)),
						with(equal(port)), with(any(byte[].class)),
						with(equal(1)));
				will(throwException(new IOException("just a test")));
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
	}

	@Test
	// (expected = IOException.class)
	public void test_SendIOException_2Block() throws IOException {
		tftpadapter = context.mock(TFTPAdapter.class);

		factory = context.mock(PacketAdapterFactory.class);
		chunker = new MockChunker(540);
		final AckPacket p = new MockAckPacket(chunker, host, port);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new SendSession(tftpadapter, host, port, chunker, factory);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendDataPacket(with(equal(host)),
						with(equal(port)), with(chunker.matchByteBlock()),
						with(equal(1)));
				will(new MockSessionAck(session, chunker, host, port));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(p));
				oneOf(tftpadapter).sendDataPacket(with(equal(host)),
						with(equal(port)), with(chunker.matchByteBlock()),
						with(equal(2)));
				will(throwException(new IOException("just a test")));
				oneOf(tftpadapter).sendErrorPacket(with(equal(host)),
						with(equal(port)), with(any(Integer.class)),
						with(equal("IOException")));
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
	}

	@Test
	public void test_SendIOException_2BlockAndSendErrorIOException()
			throws IOException {
		tftpadapter = context.mock(TFTPAdapter.class);
		host = InetAddress.getByName("10.25.4.4");
		factory = context.mock(PacketAdapterFactory.class);
		chunker = new MockChunker(540);
		final AckPacket p = new MockAckPacket(chunker, host, port);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new SendSession(tftpadapter, host, port, chunker, factory);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendDataPacket(with(equal(host)),
						with(equal(port)), with(chunker.matchByteBlock()),
						with(equal(1)));
				will(new MockSessionAck(session, chunker, host, port));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(p));
				oneOf(tftpadapter).sendDataPacket(with(equal(host)),
						with(equal(port)), with(chunker.matchByteBlock()),
						with(equal(2)));
				will(throwException(new IOException("just a test")));
				oneOf(tftpadapter).sendErrorPacket(with(equal(host)),
						with(equal(port)), with(any(Integer.class)),
						with(equal("IOException")));
				will(throwException(new IOException("send error error")));
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
	}

	@Test
	public void test_ErrorPacketHandlingOnFirstBlock() throws IOException {
		tftpadapter = context.mock(TFTPAdapter.class);
		chunker = new MockChunker(540);
		factory = context.mock(PacketAdapterFactory.class);

		final MockErrorPacket p = new MockErrorPacket(host, port,
				TFTPErrorPacket.UNDEFINED, "Theres an error on first block");
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new SendSession(tftpadapter, host, port, chunker, factory);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendDataPacket(with(equal(host)),
						with(equal(port)), with(any(byte[].class)),
						with(equal(1)));
				will(new MockErrorPacketGenerator(session, host, port,
						TFTPErrorPacket.UNDEFINED, "Error as part of test"));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(p));
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
	}

	@Test
	public void test_ErrorPacketHandlingOnSecondBlock() throws IOException {
		tftpadapter = context.mock(TFTPAdapter.class);
		chunker = new MockChunker(540);
		factory = context.mock(PacketAdapterFactory.class);
		
		final AckPacket ackpacket = new MockAckPacket(chunker, host, port);
		final MockErrorPacket errorpacket = new MockErrorPacket(host, port,
				TFTPErrorPacket.UNDEFINED, "Theres an error on second block");
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new SendSession(tftpadapter, host, port, chunker, factory);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).sendDataPacket(with(equal(host)),
						with(equal(port)), with(any(byte[].class)),
						with(equal(1)));
				will(new MockSessionAck(session, chunker, host, port));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(ackpacket));
				oneOf(tftpadapter).sendDataPacket(with(equal(host)),
						with(equal(port)), with(any(byte[].class)),
						with(equal(2)));
				will(new MockErrorPacketGenerator(session, host, port,
						TFTPErrorPacket.UNDEFINED, "Error as part of test"));
				oneOf(factory).newAdapter(with(any(TFTPPacket.class)));
				will(returnValue(errorpacket));
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
	}

	private void bytes(int size) throws IOException {
		tftpadapter = context.mock(TFTPAdapter.class);
		chunker = new MockChunker(size);
		factory = context.mock(PacketAdapterFactory.class);
		host = InetAddress.getByName("10.25.4.4");
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).setPacketHandler(
						with(any(OnPacketHandler.class)));
				oneOf(tftpadapter).setTimeoutHandler(
						with(any(OnTimeoutHandler.class)));
			}
		});
		session = new SendSession(tftpadapter, host, port, chunker, factory);
		createByteCheck(size);
		context.checking(new Expectations() {
			{
				oneOf(tftpadapter).close();
			}
		});
		session.start();
		context.assertIsSatisfied();
	}

	private void createByteCheck(int size) throws IOException {
		final int iters = chunker.getIterations();
		final AckPacket p = new MockAckPacket(chunker, host, port);
		context.checking(new Expectations() {
			{
				atLeast(iters).of(tftpadapter).sendDataPacket(
						with(equal(host)), with(equal(port)),
						with(chunker.matchByteBlock()),
						with(chunker.matchBlockNumber()));
				will(new MockSessionAck(session, chunker, host, port));
				atLeast(iters).of(factory).newAdapter(
						with(any(TFTPPacket.class)));
				will(returnValue(p));
			}
		});
	}

}
