package slightlysain.jvtftp.io;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import slightlysain.jvtftp.io.stream.StreamFactory;

public class ReplaceInputStream extends FilterInputStream {
	private StringBuffer readBuffer = new StringBuffer();
	private Map<String, String> mapping = new HashMap<String, String>();
	private boolean eof = false;

	public ReplaceInputStream(InputStream in) {
		super(in);
	}

	public ReplaceInputStream(String filename, StreamFactory factory)
			throws FileNotFoundException {
		super(factory.getFileInputStream(filename));
	}

	@Override
	public int read() throws IOException {
		int resultChar;
		if (readBuffer.length() > 0) {
			resultChar = getStartChar();
		} else {
			resultChar = super.read();
			if ('{' == resultChar) {
				int next = super.read();
				if ('%' == next) {
					StringBuffer keyBuffer = new StringBuffer();
					int last = '\0';
					int ch;
					while (('}' != (ch = super.read())) && ('%' != last)) {
						if (' ' == resultChar) {
							throw new IOException("Substition key has a space");
						} else if (-1 == ch) {
							throw new IOException(
									"Substition not completed and EOF reached");
						}
						keyBuffer.append((char) ch);
						last = ch;
					}
					keyBuffer.deleteCharAt(keyBuffer.length() - 1);
					String key = keyBuffer.toString();
					if (mapping.containsKey(key)) {
						readBuffer.append(mapping.get(key));
					} else {
						readBuffer.append("{*ERROR*");
						readBuffer.append(keyBuffer.toString());
						readBuffer.append("*ERROR*}");
					}
				} else {
					readBuffer.append((char) '{');
					readBuffer.append((char) next);
				}
				resultChar = getStartChar();
			}
		}
		if(-1 == resultChar) {
			eof = true;
		}
		return resultChar;
	}

	public void setMapping(Map<String, String> map) {
		mapping = map;
	}

	private char getStartChar() {
		char ch = readBuffer.charAt(0);
		readBuffer.deleteCharAt(0);
		return ch;
	}

	@Override
	public int read(byte[] buffer, int off, int len) throws IOException {
		if(eof) {
			return -1;
		}
		int i = 0;
		int pos = 0;
		while ((pos < len) && (-1 != (i = read()))) {
			buffer[off + pos] = (byte) i;
			pos++;
		}
		return pos;
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}

}
