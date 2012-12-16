package slightlysain.jvtftp.session;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import slightlysain.jvtftp.session.Chunker;


public class TestChunker {

	Chunker chunker;
	byte[] bytes;
	
	private ByteArrayInputStream createStream(final int size) {
		bytes = new byte[size];
		Random rand = new Random();
		rand.nextBytes(bytes);
		return new ByteArrayInputStream(bytes);
	}
	
	@Test(expected=NullPointerException.class)
	public void testChunker_NullStream() {
		chunker = new ChunkerImpl(null);
	}
	
	@Test
	public void test_0BytesStream() throws IOException {
		chunker = new ChunkerImpl(createStream(0));
		assertTrue(chunker.hasNextByte());
		assertEquals(0, chunker.getNext().length);
		assertFalse(chunker.hasNextByte());
	}
	
	@Test
	public void test_LessThan512Stream() throws IOException {
		chunker = new ChunkerImpl(createStream(14));
		assertTrue(chunker.hasNextByte());
		assertEquals(14, chunker.getNext().length);
		assertFalse(chunker.hasNextByte());
	}
	
	@Test
	public void test_512Stream() throws IOException {
		chunker = new ChunkerImpl(createStream(512));
		assertTrue(chunker.hasNextByte());
		assertEquals(512, chunker.getNext().length);
		assertTrue(chunker.hasNextByte());
		assertEquals(0, chunker.getNext().length);
		assertFalse(chunker.hasNextByte());
	}
	
	@Test
	public void test_GreateThan512Stream() throws IOException {
		int count = 560;
		chunker = new ChunkerImpl(createStream(count));
		assertTrue(chunker.hasNextByte());
		assertEquals(512, chunker.getNext().length);
		assertTrue(chunker.hasNextByte());
		assertEquals(count - 512, chunker.getNext().length);
		assertFalse(chunker.hasNextByte());
	}
	
	@Test
	public void test_LargeStream() throws IOException {
		int count = 2070;
		byte[] testbytes = new byte[count];
		chunker = new ChunkerImpl(createStream(count));
		//Arrays.
		int pos = 0;
		while(chunker.hasNextByte()) {
			byte[] b = chunker.getNext();
			System.out.println("----chunker b.l" + b.length);
			for(int i=0; i < b.length; i++) {
				//System.out.println("pos + i" + (pos + i));
				testbytes[pos + i] = b[i];
			}
			pos += b.length;
			System.out.println("----pos" + pos);
		}
		assertFalse(chunker.hasNextByte());
		assertArrayEquals(bytes, testbytes);
	}
}
