package slightlysain.mock;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestMockChunker {

	@Test
	public void testMockChunker() throws IOException {
		final int size = 512;
		byte[] b = new byte[size];
		new Random().nextBytes(b);	
		MockChunker chunker = new MockChunker(b);
		int total = 0;
		int iteration = 0;
		while(chunker.hasNextByte()) {
			byte[] tmp = chunker.getNext();
			assertEquals(tmp, chunker.getLast());
			int len = tmp.length; 
			System.out.println("i=" + iteration + ",len=" + len);
			assertEquals(iteration, chunker.getLastBlock());
			total += len;
			iteration++;
		}
		assertEquals(2, iteration);
		assertEquals(size, total);
	}
	
	@Test
	public void testMockChunkerZero() throws IOException {
		final int size = 0;
		byte[] b = new byte[size];
		new Random().nextBytes(b);	
		MockChunker chunker = new MockChunker(b);
		int total = 0;
		int iteration = 0;
		while(chunker.hasNextByte()) {
			byte[] tmp = chunker.getNext();
			assertEquals(tmp, chunker.getLast());
			int len = tmp.length; 
			System.out.println("i=" + iteration + ",len=" + len);
			total += len;
			assertEquals(iteration, chunker.getLastBlock());
			iteration++;
		}
		assertEquals(1, iteration);
		assertEquals(1, chunker.getIterations());
		assertEquals(size, total);
	}
	
	@Test
	public void testMockSize500() throws IOException {
		int size = 500;
		MockChunker chunker = new MockChunker(size);
		int total = 0;
		int iteration = 0;
		while(chunker.hasNextByte()) {
			byte[] tmp = chunker.getNext();
			assertEquals(tmp, chunker.getLast());
			int len = tmp.length; 
			System.out.println("i=" + iteration + ",len=" + len);
			total += len;
			assertEquals(iteration, chunker.getLastBlock());
			iteration++;
		}
		assertEquals(1, iteration);
		assertEquals(1, chunker.getIterations());
		assertEquals(size, total);
		
	}
	
	@Test
	public void testMockSize5000() throws IOException {
		int size = 5000;
		MockChunker chunker = new MockChunker(size);
		int total = 0;
		int iteration = 0;
		int expectediters = size / 512 + 1;
		while(chunker.hasNextByte()) {
			byte[] tmp = chunker.getNext();
			assertEquals(tmp, chunker.getLast());
			int len = tmp.length; 
			System.out.println("i=" + iteration + ",len=" + len);
			total += len;
			assertEquals(iteration, chunker.getLastBlock());
			iteration++;
		}
		assertEquals(expectediters, iteration);
		assertEquals(expectediters, chunker.getIterations());
		assertEquals(size, total);
		
	}


}
