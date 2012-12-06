package slightlysain.jvtftp.session;

import java.io.IOException;

public interface Chunker {

	public abstract boolean hasNextByte();

	public abstract byte[] getNext() throws IOException;

}