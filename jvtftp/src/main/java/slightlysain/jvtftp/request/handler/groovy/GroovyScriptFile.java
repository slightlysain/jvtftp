package slightlysain.jvtftp.request.handler.groovy;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import slightlysain.jvtftp.request.handler.RequestHandlerPriority;

public interface GroovyScriptFile {

	public abstract String getName();

	public abstract void run(Binding binding)
			throws ResourceException, ScriptException;

	public abstract RequestHandlerPriority getPriority()
			throws PriorityCommentNotFoundException,
			InvalidPriorityCommentException;

}