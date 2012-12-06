package slightlysain.jvtftp.request.handler.groovy;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.NoPriorityException;
import slightlysain.jvtftp.request.handler.RequestHandler;
import slightlysain.jvtftp.request.router.RequestHandlerPriority;
import slightlysain.mock.DummyRequest;

import static slightlysain.jvtftp.request.router.RequestHandlerPriority.*;

public class TestGroovyScriptFile {
	private GroovyScriptEngine groovyScriptEngine;
	private Mockery context;
	private String[] roots;
	private File testFile;
	private PrintWriter testFileWriter;
	private String testFilename;
	private GroovyScriptFile scriptFile;
	private RequestHandlerPriority priority;
	private PrintWriter scriptOutput;
	private PipedOutputStream outputStream;
	private PipedInputStream inputStream;
	private Scanner scriptScanner;

	@Before
	public void newContext() throws IOException {
		context = new Mockery();
		setupOutputInputStreams();
		scriptOutput = new PrintWriter(outputStream, true);
	}

	private void setupOutputInputStreams() throws IOException {
		inputStream = new PipedInputStream();
		outputStream = new PipedOutputStream(inputStream);
		scriptScanner = new Scanner(inputStream);
	}
	
	private void newTestFile(String content) throws FileNotFoundException,
			IOException {
		tempFile();
		testFilename = testFile.getName();
		testFileWriter.println(content);
		testFileWriter.close();
	}

	private void tempFile() throws IOException, FileNotFoundException {
		testFile = File.createTempFile("groovyRequestHandlerTest", ".groovy");
		System.out.println(testFile.getAbsolutePath());
		testFile.deleteOnExit();
		testFileWriter = new PrintWriter(new FileOutputStream(testFile));
		roots = new String[] { testFile.getParent() };
	}

	private void setupEngine() throws IOException {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("slightlysain.jvtftp.request.handler.groovy.AbstractGroovyRequestScript");
		cc.setOutput(scriptOutput);
		//cc.
		GroovyClassLoader gcl = new GroovyClassLoader(
				ClassLoader.getSystemClassLoader(), cc);
		groovyScriptEngine = new GroovyScriptEngine(roots, gcl);
	}

	private void testGetPriorityComment(String content)
			throws FileNotFoundException, IOException,
			PriorityCommentNotFoundException, InvalidPriorityCommentException {
		newScriptFile(content);
		priority = scriptFile.getPriority();
	}

	private void newScriptFile(String content) throws FileNotFoundException,
			IOException, PriorityCommentNotFoundException, InvalidPriorityCommentException {
		newTestFile(content);
		setupEngine();
		scriptFile = new GroovyScriptFileImpl(groovyScriptEngine, testFilename);
	}

	@Test(expected = InvalidPriorityCommentException.class)
	public void testEmptyPriorityCommentGetPriority()
			throws FileNotFoundException, IOException, NoPriorityException {
		testGetPriorityComment("//priority");
	}

	@Test(expected = InvalidPriorityCommentException.class)
	public void testWhitespacePriorityComment() throws FileNotFoundException,
			IOException, NoPriorityException {
		testGetPriorityComment("//priority        ");
	}

	@Test
	public void testAllGetPriorityLevels() throws IOException,
			NoPriorityException {
		RequestHandlerPriority[] priorities = RequestHandlerPriority.values();
		for (RequestHandlerPriority p : priorities) {
			testGetPriorityComment("//priority " + p.toString());
			assertEquals(p, priority);
		}
	}

	@Test(expected = PriorityCommentNotFoundException.class)
	public void testNoPriorityCommentException() throws FileNotFoundException,
			PriorityCommentNotFoundException, InvalidPriorityCommentException,
			IOException {
		testGetPriorityComment("//notpriority\n//othercomment\n//other comment two");
	}

	@Test
	public void testCommentSecondLine() throws FileNotFoundException,
			PriorityCommentNotFoundException, InvalidPriorityCommentException,
			IOException {
		RequestHandlerPriority p = LOW_COMMAND;
		testGetPriorityComment("//hello\n//priority " + p.toString()
				+ "\n//more stuff");
		assertEquals(p, priority);
	}

	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ResourceException
	 * @throws ScriptException
	 * @throws InvalidPriorityCommentException 
	 * @throws PriorityCommentNotFoundException 
	 */
	@Ignore
	public void testScriptFileRun() throws FileNotFoundException, IOException,
			ResourceException, ScriptException, PriorityCommentNotFoundException, InvalidPriorityCommentException {
		RequestHandler hand = new RequestHandler() {
			public RequestHandlerPriority getPriority()
					throws NoPriorityException {
				// do nothing
				return null;
			}

			public void setNext(RequestHandler requestHandler) {
				// do nothing
			}

			public void execute(Request request) {
				// do nothing
			}

			public void skip(Request request) {
				// do nothing
			}
		};
		DummyRequest dreq = new DummyRequest();
		Binding binding = new RequestHandlerBinding(dreq, null, scriptOutput);
		//binding.setVariable("request", dreq);
		//binding.setVariable("handler", hand);
		
		final String TEST_STRING = "testing run";
		
		String filecontent = "println \""+ TEST_STRING + "\"";
		newScriptFile(filecontent);
		scriptFile.run(binding);
		//scriptOutput.println(TEST_STRING + "\n");
		scriptOutput.close();
		
		String line = "";
		if(scriptScanner.hasNext()) {
			line = scriptScanner.nextLine();
		}
		assertFalse(scriptScanner.hasNext()); 
		assertEquals(TEST_STRING, line);
	}
	
	
	
	// // @Test
	// public void testExecute() {
	//
	// String content = "//priority LOW_COMMAND\n";
	//
	// System.out.println("TestExecute");
	// }
	//
	// public void testNoScriptFile() {
	// System.out.println("Testnooscript");
	// }
	//
	// public void testExecuteNoScript() {
	// }

}
