package slightlysain.jvtftp.request.handler.groovy;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.router.RequestHandlerPriority;

public class GroovyScriptFileImpl implements GroovyScriptFile {
	private URL[] roots;
	private String scriptName;
	private File scriptFile;
	private Logger log = LoggerFactory.getLogger(getClass());
	private String priorityCommentString;
	private RequestHandlerPriority priority = null;
	private GroovyScriptEngine engine;

	public GroovyScriptFileImpl(GroovyScriptEngine gse, String scriptName)
			throws PriorityCommentNotFoundException,
			InvalidPriorityCommentException, FileNotFoundException {
		this.roots = gse.getGroovyClassLoader().getURLs();
		this.scriptName = scriptName;
		this.engine = gse;
		setPriority();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slightlysain.jvtftp.request.handler.groovy.GroovyScriptFile#getName()
	 */
	public String getName() {
		return scriptName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slightlysain.jvtftp.request.handler.groovy.GroovyScriptFile#run(groovy
	 * .util.GroovyScriptEngine, groovy.lang.Binding)
	 */
	public void run(Binding binding) throws ResourceException, ScriptException {
		engine.run(scriptName, binding);
	}

	public RequestHandlerPriority getPriority() {
		return priority;
	}

	public void setPriority() throws PriorityCommentNotFoundException,
			InvalidPriorityCommentException, FileNotFoundException {
		for (URL url : roots) {
			try {
				URI uri = url.toURI();
				if (isFileURI(uri)) {
					scriptFile = new File(new File(uri), scriptName);
					if (scriptFile.isDirectory()) {
						continue;
					}
					if (scriptFile.exists()) {
						getPriorityComment();
						convertCommentToPriority();
						return;
					}
				} else {
					log.error("script dir has not got file scheme, cannot get priority:"
							+ uri.toString());
				}
			} catch (URISyntaxException e) {
				// should never reach here
				log.error("Bad root URLs passed to GroovyScriptEngine file:"
						+ url.toString(), e);
			}
		}
		throw new FileNotFoundException();
	}

	private void convertCommentToPriority()
			throws InvalidPriorityCommentException {
		String prioritySection = priorityCommentString.substring(10).trim();
		if (prioritySection.length() < 0) {
			throw new InvalidPriorityCommentException();
		}
		try {
			priority = RequestHandlerPriority.valueOf(prioritySection);
		} catch (IllegalArgumentException e) {
			throw new InvalidPriorityCommentException();
		}
	}

	private void getPriorityComment() throws PriorityCommentNotFoundException {
		Scanner scan = null;
		try {
			scan = new Scanner(scriptFile);
		} catch (FileNotFoundException e) {
			// this should never happen as file existence is already checked.
			log.error("Could not get comment, file not found", e);
		}
		while (scan.hasNext()) {
			String line = scan.nextLine();
			if (line.startsWith("//priority")) {
				priorityCommentString = line;
				return;
			}
		}
		throw new PriorityCommentNotFoundException();
	}

	private boolean isFileURI(URI i) {
		return i.getScheme().equals("file");
	}
}
