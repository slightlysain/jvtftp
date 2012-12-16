package slightlysain.initrd;

import groovy.lang.Closure;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.compress.changes.ChangeSetPerformer;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import slightlysain.jvtftp.request.handler.groovy.AbstractScript;
import slightlysain.jvtftp.stream.JvtftpInput;
import slightlysain.jvtftp.stream.StreamFactory;

public class Initrd implements JvtftpInput {
	private static final String FILE_MODE = "100777";
	private ChangeSet changeset;
	private String filename;
	OutputStream outputStream;
	PipedInputStream pipedInput;
	StreamFactory streamFactory;

	public Initrd(String filename, StreamFactory streamFactory)
			throws IOException {
		this.filename = filename;
		this.streamFactory = streamFactory;
		pipedInput = new PipedInputStream();
		outputStream = new PipedOutputStream(pipedInput);
		changeset = new ChangeSet();
	}

	public InputStream getInput() {
		return pipedInput;
	}

	public void add(String filename) throws FileNotFoundException {
		File file = streamFactory.getInputFile(filename);
		InputStream input = new FileInputStream(file);
		CpioArchiveEntry entry = new CpioArchiveEntry(filename);
		int mode = Integer.parseInt(FILE_MODE, 8);
		entry.setMode(mode);
		entry.setSize(file.length());
		changeset.add(entry, input);
	}

	public void add(String filename, String entryname)
			throws FileNotFoundException {
		File file = streamFactory.getInputFile(filename);
		InputStream input = new FileInputStream(file);
		CpioArchiveEntry entry = new CpioArchiveEntry(entryname);
		int mode = Integer.parseInt(FILE_MODE, 8);
		entry.setMode(mode);
		entry.setSize(file.length());
		changeset.add(entry, input);
	}

	public void add(String filename, Closure<?> clos) throws IOException {
		AbstractScript script = (AbstractScript) clos.getOwner();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(outputStream);
		script.setPrintStream(out);
		CpioArchiveEntry entry = new CpioArchiveEntry(filename);
		int mode = Integer.parseInt(FILE_MODE, 8);
		entry.setMode(mode);
		clos.call();
		outputStream.flush();
		outputStream.close();
		byte[] output = outputStream.toByteArray();
		entry.setSize(output.length);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(output);
		changeset.add(entry, inputStream);
		inputStream.close();
		script.resetPrintStream();
	}

	public void add(String filename, JvtftpInput input) {
		// TODO:finish this function possibly change JvtftpInput interface or
		// create new interface
		// ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		// InputStream inputStream = input.getInput();
		// CpioArchiveEntry entry = new CpioArchiveEntry(filename);
		// int mode = Integer.parseInt(FILE_MODE, 8);
		// entry.setMode(mode);

	}

	public void go() throws IOException {
		// input streams
		InputStream fileIn = streamFactory.getFileInputStream(filename);
		BufferedInputStream bufferdIn = new BufferedInputStream(fileIn);
		GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(
				bufferdIn);
		CpioArchiveInputStream cpioInputStream = new CpioArchiveInputStream(
				gzipIn);
		// output streams
		GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(
				outputStream);
		CpioArchiveOutputStream archiveOutput = new CpioArchiveOutputStream(
				gzipOut);

		// cpioInputStream
		ChangeSetPerformer csp = new ChangeSetPerformer(changeset);
		csp.perform(cpioInputStream, archiveOutput);
		cpioInputStream.close();
		fileIn.close();
		gzipIn.close();
		cpioInputStream.close();
		archiveOutput.close();
		gzipOut.close();
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
