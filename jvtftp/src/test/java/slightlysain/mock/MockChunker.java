package slightlysain.mock;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import slightlysain.jvtftp.session.Chunker;

public class MockChunker implements Chunker {

	byte[] bytes, last;
	int pos;
	int itercount;

	MockChunker instance;

	public MockChunker(byte[] by) {
		this.bytes = by;
		pos = 0;
		itercount = 0;
		instance = this;
	}

	public MockChunker(int size) {
		bytes = new byte[size];
		new Random().nextBytes(bytes);
		pos = 0;
		itercount = 0;
		instance = this;
	}

	public boolean hasNextByte() {
		return pos <= bytes.length;
	}

	public byte[] getLast() {
		return last;
	}

	public int getLastBlock() {
		return itercount - 1;
	}

	public int getIterations() {
		return bytes.length / 512 + 1;
	}

	public byte[] getNext() throws IOException {
		if (pos == bytes.length) {
			pos += 1;
			last = new byte[0];
		} else if ((pos + 512) > bytes.length) {
			last = Arrays.copyOf(bytes, bytes.length - pos);
		} else {
			boolean fullchunk = ((bytes.length - pos) > 512);
			int len = fullchunk ? 512 : (bytes.length - pos);
			last = Arrays.copyOfRange(bytes, pos, (pos + 512));
		}
		pos += 512;
		itercount++;
		return last;
	}

	public TypeSafeMatcher<byte[]> matchByteBlock() {
		return new TypeSafeMatcher<byte[]>() {
			Object item;
			boolean called = false;

			public void describeTo(Description description) {
				byte[] i = (byte[]) item;
				description.appendText("[" + called + "]");
			}

			@Override
			public boolean matchesSafely(byte[] item) {
				boolean result = item.equals(instance.getLast());
				return result;
			}
		};
	}

	public TypeSafeMatcher<Integer> matchBlockNumber() {
		return new TypeSafeMatcher<Integer>() {
			public boolean called = false;

			public void describeTo(Description description) {
				description.appendText(new Integer(instance.getLastBlock() + 1)
						.toString() + ":" + called);
			}

			@Override
			public boolean matchesSafely(Integer item) {
				called = true;
				boolean match = item.equals(instance.getLastBlock() + 1);
				return match;
			}
		};
	}

}
