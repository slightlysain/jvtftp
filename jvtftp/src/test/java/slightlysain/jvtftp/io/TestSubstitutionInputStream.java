package slightlysain.jvtftp.io;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestSubstitutionInputStream {
	/**
	 * these test need to be updated and refractored to function as unit tests
	 * without human interaction being needed to confirm the results of the
	 * tests
	 */

	@Test
	public void testSubstitution() throws IOException {
		byte[] bys = "before username {%username%} after".getBytes();
		ByteArrayInputStream input = new ByteArrayInputStream(bys);
		ReplaceInputStream sis = new ReplaceInputStream(input);
		Map<String, String> m = new HashMap<String, String>();
		m.put("username", "diskless");
		sis.setMapping(m);
		int ch = 0;
		while (-1 != (ch = sis.read())) {
			System.out.print((char) ch);
		}
		sis.close();
	}

	@Test
	public void testSmallArrayRead() throws IOException {
		byte[] bys = "before username {%username%} after".getBytes();
		ByteArrayInputStream input = new ByteArrayInputStream(bys);
		// FileInputStream fis = new FileInputStream()
		ReplaceInputStream sis = new ReplaceInputStream(input);
		Map<String, String> m = new HashMap<String, String>();
		m.put("username", "diskless");
		sis.setMapping(m);
		int ch = 0;
		byte[] bytes = new byte[5];
		sis.read(bytes);
		input = new ByteArrayInputStream(bytes);
		while (-1 != (ch = sis.read())) {
			System.out.print((char) ch);
		}
		sis.close();
	}

	@Test
	public void testOtherOffsetRead() throws IOException {
		byte[] expected = "0123456789".getBytes();
		ByteArrayInputStream input = new ByteArrayInputStream(expected);
		ReplaceInputStream sis = new ReplaceInputStream(input);
		byte[] result = new byte[expected.length];
		sis.read(result, 0, 5);
		sis.read(result, 5, 5);
		sis.close();
		assertArrayEquals(expected, result);
	}

	@Test
	public void testArrayRead() throws IOException {
		byte[] expected = "before username diskless after".getBytes();
		byte[] bys = "before username {%username%} after".getBytes();
		ByteArrayInputStream input = new ByteArrayInputStream(bys);
		ReplaceInputStream sis = new ReplaceInputStream(input);
		Map<String, String> m = new HashMap<String, String>();
		m.put("username", "diskless");
		sis.setMapping(m);
		byte[] result = new byte[expected.length];
		sis.read(result);
		sis.close();
		assertArrayEquals(expected, result);
	}

	@Test
	public void testArray() throws IOException {
		byte[] expected = new byte[1024];
		Random rand = new Random();
		rand.nextBytes(expected);
		InputStream[] input = { new ByteArrayInputStream(expected),
				new ByteArrayInputStream(expected) };
		ReplaceInputStream replace = new ReplaceInputStream(input[0]);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IOUtils.copy(replace, output);
		byte[] outputbytes = output.toByteArray(); // new byte[expected.length];
		// replace.read(outputbytes);
		assertArrayEquals(expected, outputbytes);
	}

	@Test
	public void testUsernameReplace() throws IOException {
		String filename = "/home/harry/git/jvtftp/jvtftp/outgoing/squeeze/squeeze-preseed.cfg";
		FileInputStream fis = new FileInputStream(filename);
		ReplaceInputStream replace = new ReplaceInputStream(fis);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", "diskless");
		replace.setMapping(map);
		add("preseed.cfg", replace);
	}

	public void add(String filename, InputStream in) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.out.println("copy start");
		System.out.println(new Date());
		IOUtils.copy(in, outputStream);
		System.out.println(new Date());
		System.out.println("copy stop");
		byte[] output = outputStream.toByteArray();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(output);
		IOUtils.copy(inputStream, System.out);
		inputStream.close();
	}

}
