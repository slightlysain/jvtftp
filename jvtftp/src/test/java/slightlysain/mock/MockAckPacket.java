package slightlysain.mock;

import java.net.InetAddress;

import slightlysain.jvtftp.packetadapter.AckPacket;

public class MockAckPacket implements AckPacket {
	private MockChunker chunker;
	private InetAddress client;
	private int port;
	
	
	public MockAckPacket(MockChunker chunker, InetAddress client, int port) {
		this.chunker = chunker;
		this.client = client;
		this.port = port;
	}

	public int getBlockNumber() {
		return chunker.getLastBlock() + 1;
	}

	public boolean isAckowledgment() {
		return true;
	}

	public boolean isData() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isReadRequest() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWriteRequest() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isError() {
		// TODO Auto-generated method stub
		return false;
	}

	public InetAddress getClient() {
		return client;
	}

	public int getPort() {
		return port;
	}

	public boolean isClient(InetAddress client, int port) {
		return this.client.equals(client) && this.port == port;
	}

}
