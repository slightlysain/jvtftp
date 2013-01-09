package slightlysain.initrd;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Ignore;
import org.junit.Test;

import slightlysain.jvtftp.io.ReplaceInputStream;
import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.io.stream.StreamFactoryImpl;

public class TestInitrd {
	Initrd initrd;

	@Test
	public void testCreation() throws Exception {
		System.out.println(new Date().toString());
		StreamFactory factory = new StreamFactoryImpl(
				"/home/harry/git/jvtftp/jvtftp/incoming",
				"/home/harry/git/jvtftp/jvtftp/outgoing");
		initrd = new Initrd("initrd.gz", factory);
		InputStream in = initrd.getInput();
		System.out.println(new Date().toString());
	}

	/**
	 * long test this test needs updating to check for error conditions and that
	 * the output file conforms to what is expected. This test is also time
	 * consuming and has been diabled
	 * @throws Exception 
	 */
	@Ignore
	//@Test
	public void testGenerateOutput() throws Exception {
		System.out.println(new Date().toString());
		StreamFactory factory = new StreamFactoryImpl(
				"/home/harry/git/jvtftp/jvtftp/incoming",
				"/home/harry/git/jvtftp/jvtftp/outgoing");
		initrd = new Initrd("initrd.gz", factory);
		testAddStream();
		InputStream in = initrd.getInput();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(initrd);
		int ch = 0;
		FileOutputStream output = new FileOutputStream("/home/harry/output.gz");
		while (-1 != (ch = in.read())) {
			output.write(ch);
		}
		output.close();
		System.out.println(new Date().toString());
	}

	public void testAddStream() throws IOException {
		String filename = "/home/harry/git/jvtftp/jvtftp/outgoing/squeeze/squeeze-preseed.cfg";
		FileInputStream fis = new FileInputStream(filename);
		ReplaceInputStream replace = new ReplaceInputStream(fis);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("username", "diskless");
		replace.setMapping(map);
		initrd.add("preseed.cfg", replace);
	}
}