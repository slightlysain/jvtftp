package slightlysain.mock;

import java.net.InetAddress;

import slightlysain.jvtftp.packetadapter.DataPacket;

public class MockDataPacket implements DataPacket {
	InetAddress client;
	int port;
	byte[] data;
	int block;

	
	public MockDataPacket(InetAddress client, int port, byte[] data, int block) {
		super();
		this.client = client;
		this.port = port;
		this.data = data;
		this.block = block;
	}
	
	public boolean isAckowledgment() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isData() {
		return true;
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
		// TODO Auto-generated method stub
		return null;
	}

	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isClient(InetAddress client, int port) {
		return this.client.equals(client) && this.port == port;
	}

	public byte[] getData() {
		return data;
	}

	public int getDataLength() {
		return data.length;
	}

	public int getBlockNumber() {
		return block;
	}

}
