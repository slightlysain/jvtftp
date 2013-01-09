package slightlysain.initrd;

import groovy.lang.Closure;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Date;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioConstants;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.compress.changes.ChangeSetPerformer;
import org.apache.commons.compress.changes.ChangeSetResults;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import slightlysain.externalprocess.FileTypeLookup;
import slightlysain.externalprocess.TestFileTypeLookup;
import slightlysain.jvtftp.io.stream.JvtftpInput;
import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.request.handler.groovy.AbstractScript;

public class Initrd implements JvtftpInput {
	private static final String FILE_MODE = "100777";
	private static final int mode = Integer.parseInt(FILE_MODE, 8);
	private static final boolean replaceExistingFile = true;
	private ChangeSet changeset;
	private ChangeSet delChangeSet;
	private String filename;
	private OutputStream outputStream;
	private PipedInputStream pipedInput;
	private StreamFactory streamFactory;
	private InputStream fileInputStream;
	private File rdfile;
	private InitdChangePerformer changePerformer;

	public Initrd(String filename, StreamFactory streamFactory)
			throws Exception {
		this.filename = filename;
		this.streamFactory = streamFactory;
		pipedInput = new PipedInputStream();
		outputStream = new PipedOutputStream(pipedInput);
		changeset = new ChangeSet();
		delChangeSet = new ChangeSet();
		rdfile = streamFactory.getInputFile(filename);
		if (filename.endsWith(".gz")) {
			changePerformer = new GzipInitrdChangePerformer(outputStream);
		} else {
			FileTypeLookup typeLookup = new FileTypeLookup(rdfile);
			if (typeLookup.getType().equals("XZ")) {
				changePerformer = new XZInitrdChangePerformer(outputStream);
			} else {
				throw new Exception("unknown filetype");
			}
		}
	}

	public InputStream getInput() {
		return pipedInput;
	}

	private CpioArchiveEntry entry(String filename, long size, String modeStr) {
		final int format = CpioConstants.C_ISREG;
		CpioArchiveEntry entry = new CpioArchiveEntry(filename, size);
		//entry.setSize(size);
		int mode = Integer.parseInt(modeStr, 8);
		entry.setMode(mode);
		System.out.println("fmode:" + filename + "::rf" + entry.isRegularFile());
		System.out.println("fmode:" + filename + "::sl" + entry.isSymbolicLink());
		System.out.println("fmode:" + filename + "::bd" + entry.isBlockDevice());
		System.out.println("fmode:" + filename + "::cd" + entry.isCharacterDevice());
		System.out.println("fmode:" + filename + "::d" + entry.isDirectory());
		System.out.println("fmode:" + filename + "::n" + entry.isNetwork());
		System.out.println("fmode:" + filename + "::d" + entry.isPipe());
		System.out.println("fmode:" + filename + "::d" + entry.isSocket());
		Date d = new Date();
		//entry.
		//entry.setTime(d.getTime());
		//entry.se
		//entry.setTime(System.currentTimeMillis());
		//entry.setGID(0);
		//entry.setUID(0);
		return entry;
	}

	public void add(String filename) throws FileNotFoundException {
		File file = streamFactory.getInputFile(filename);
		InputStream input = new FileInputStream(file);
		CpioArchiveEntry entry = entry(filename, file.length(), FILE_MODE);
		changeset.add(entry, input, replaceExistingFile);
	}

	public void add(String filename, String entryname)
			throws FileNotFoundException {
		File file = streamFactory.getInputFile(filename);
		InputStream input = new FileInputStream(file);
		CpioArchiveEntry entry = entry(entryname, file.length(), FILE_MODE);
		changeset.add(entry, input, replaceExistingFile);
	}

	public void add(String filename, InputStream in) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IOUtils.copy(in, outputStream);
		byte[] output = outputStream.toByteArray();
		CpioArchiveEntry entry = entry(filename, output.length, FILE_MODE);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(output);
		changeset.add(entry, inputStream, replaceExistingFile);
	}
	
	public void add(String filename, Closure<?> clos) throws IOException {
		this.add(filename, clos, FILE_MODE);
	}

	public void add(String filename,  Closure<?> clos, String fileMode) throws IOException {
		AbstractScript script = (AbstractScript) clos.getOwner();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(outputStream);
		script.setPrintStream(out);	
		clos.call();
		outputStream.flush();
		outputStream.close();
		byte[] output = outputStream.toByteArray();
		CpioArchiveEntry entry = entry(filename, output.length, fileMode);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(output);
		changeset.add(entry, inputStream, replaceExistingFile);
		inputStream.close();
		script.resetPrintStream();
	}

	public void del(String filename) {
		delChangeSet.delete(filename);
	}

	public void go() throws IOException {
		InputStream input = new FileInputStream(rdfile);
		fileInputStream = new BufferedInputStream(input);
		ChangeSetResults csr = changePerformer.performChange(fileInputStream, changeset, delChangeSet);
		for(String s : csr.getAddedFromChangeSet()) {
			System.out.println("added from changeset" + s);
		}
		for(String s : csr.getAddedFromStream()) {
			System.out.println("added from stream" + s);
		}
		input.close();
		outputStream.close();
	}

	public void run() {
		try {
			go();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
