package slightlysain.mock;

import static org.apache.commons.net.tftp.TFTPErrorPacket.ACCESS_VIOLATION;
import static org.apache.commons.net.tftp.TFTPErrorPacket.FILE_EXISTS;
import static org.apache.commons.net.tftp.TFTPErrorPacket.FILE_NOT_FOUND;
import static org.apache.commons.net.tftp.TFTPErrorPacket.ILLEGAL_OPERATION;
import static org.apache.commons.net.tftp.TFTPErrorPacket.NO_SUCH_USER;
import static org.apache.commons.net.tftp.TFTPErrorPacket.OUT_OF_SPACE;
import static org.apache.commons.net.tftp.TFTPErrorPacket.UNDEFINED;
import static org.apache.commons.net.tftp.TFTPErrorPacket.UNKNOWN_TID;

import java.net.InetAddress;

import slightlysain.jvtftp.packetadapter.ErrorPacket;

public class MockErrorPacket implements ErrorPacket {
	InetAddress client;
	int port;
	int code;
	String message;

	
	public MockErrorPacket(InetAddress client, int port, int code,
			String message) {
		super();
		this.client = client;
		this.port = port;
		this.code = code;
		this.message = message;
	}

	
	public boolean isAckowledgment() {
		// TODO Auto-generated method stub
		return false;
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
		return true;
	}

	public InetAddress getClient() {
		return client;
	}

	public int getPort() {
		return port;
	}

	public boolean isClient(InetAddress client, int port) {
		boolean result1 = this.client.equals(client);
		boolean result2 = this.port == port;
		return result1 && result2;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getCodeString() {
		int code = getCode();
		switch (code) {
		case ACCESS_VIOLATION:
			return "ACCESS VIOLATION";
		case FILE_EXISTS:
			return "FILE EXISTS";
		case FILE_NOT_FOUND:
			return "FILE NOT FOUND";
		case ILLEGAL_OPERATION:
			return "ILLEGAL OPERATION";
		case NO_SUCH_USER:
			return "NO SUCH USER";
		case OUT_OF_SPACE:
			return "OUT_OF_SPACE";
		case UNDEFINED:
			return "UNDEFINED";
		case UNKNOWN_TID:
			return "UNKNOWN TID";
		default:
			throw new IndexOutOfBoundsException("error code does not exist");
		}
	}

	public boolean isOutOfSpace() {
		return (getCode() == OUT_OF_SPACE);
	}

}
