package slightlysain.jvtftp.session;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class ChunkerImpl implements Chunker {
	
	private InputStream input;
	private boolean eof;
	
	private final static int CHUNK_SIZE = 512;
	
	ChunkerImpl(InputStream input) {
		if(null == input) {
			throw new NullPointerException();
		}
		this.input = input;
		eof = false;
	}
	
	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.session.Chunker#hasNextByte()
	 */
	public boolean hasNextByte() {
		return !eof;
	}

	/* (non-Javadoc)
	 * @see slightlysain.jvtftp.session.Chunker#getNext()
	 */
	public byte[] getNext() throws IOException {
		byte[] bytes = new byte[512];
		int offset = 0;
		int result;
		int remaining = CHUNK_SIZE;
		do {
			result = input.read(bytes, offset, remaining);
			if(-1 == result) {
				eof = true;
				input.close();
				break;
			} 
			remaining -= result;
			offset += result;
		} while (remaining > 0);
		if(eof) {
			bytes = Arrays.copyOf(bytes, offset);
		}			
		return bytes;
	}

}
