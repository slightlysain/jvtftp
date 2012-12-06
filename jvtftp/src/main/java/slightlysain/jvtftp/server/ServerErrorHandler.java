package slightlysain.jvtftp.server;

import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is delegated the responsibility of handling error packets that are
 * received by the server and are not connected to a session. This class
 * converts the error packet to a string which can be logged. If an error which
 * needs action to be taken is detected the action should be handled by this
 * class.
 * 
 * @author slightlysain
 * 
 */
public class ServerErrorHandler {

	Logger log = LoggerFactory.getLogger(getClass());

	public ServerErrorHandler(TFTPErrorPacket packet) {
		String error = getError(packet.getError());
		log.error("Sorry, error packet recieved, code:" + error + " message:"
				+ packet.getMessage() + " host:"
				+ packet.getAddress().getHostAddress());
	}

	/**
	 * Converts error code to string
	 * 
	 * @param error
	 *            integer 0 to 7
	 * @return string corresponding to the error code
	 */
	private String getError(final int error) {
		if (error < 0 || error > 7) {
			throw new IndexOutOfBoundsException();
		}

		switch (error) {

		case TFTPErrorPacket.ACCESS_VIOLATION:
			return "ACCESS_VIOLATION";
		case TFTPErrorPacket.FILE_EXISTS:
			return "FILE_EXISTS";
		case TFTPErrorPacket.FILE_NOT_FOUND:
			return "FILE_NOT_FOUND";
		case TFTPErrorPacket.ILLEGAL_OPERATION:
			return "ILLEGAL_OPERATION";
		case TFTPErrorPacket.NO_SUCH_USER:
			return "NO_SUCH_USER";
		case TFTPErrorPacket.OUT_OF_SPACE:
			return "OUT_OF_SPACE";
		case TFTPErrorPacket.UNDEFINED:
			return "UNDEFINED";
		case TFTPErrorPacket.UNKNOWN_TID:
			return "UNKNOWN_TID";
		default:
			return "Unknown error index";

		}
	}

}
