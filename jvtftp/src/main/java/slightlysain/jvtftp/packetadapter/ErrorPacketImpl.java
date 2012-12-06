package slightlysain.jvtftp.packetadapter;

import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.apache.commons.net.tftp.TFTPPacket;
import static org.apache.commons.net.tftp.TFTPErrorPacket.*;

public class ErrorPacketImpl extends AbstractPacketAdapter implements ErrorPacket {
	private TFTPErrorPacket packet;

	public ErrorPacketImpl(TFTPPacket p) {
		super(p);
		packet = (TFTPErrorPacket) p;
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.packetadapter.ErrorPacket#getCode()
	 */
	public int getCode() {
		return packet.getError();
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.packetadapter.ErrorPacket#getMessage()
	 */
	public String getMessage() {
		return packet.getMessage();
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.packetadapter.ErrorPacket#getCodeString()
	 */
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
	
	@Override
	public boolean isError() {
		return true;
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.packetadapter.ErrorPacket#isOutOfSpace()
	 */
	public boolean isOutOfSpace() {
		return (getCode() == OUT_OF_SPACE);
	}
}
