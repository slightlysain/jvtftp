package slightlysain.jvtftp.request.router;

import java.io.FileNotFoundException;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.AbstractRequest;
import slightlysain.jvtftp.request.UnknownRequestTypeException;
import slightlysain.jvtftp.request.handler.groovy.InvalidPriorityCommentException;
import slightlysain.jvtftp.request.handler.groovy.PriorityCommentNotFoundException;

public interface RequestRouter {

	public abstract boolean isScriptLoaded(String script);

	public abstract void loadScript(String script)
			throws ScriptAlreadyLoadedException, CouldNotLoadScriptException,
			PriorityCommentNotFoundException, InvalidPriorityCommentException,
			FileNotFoundException;

	public abstract void unloadScript(String script)
			throws ScriptNotAlreadyLoadedException,
			CouldNotRemoveScriptException;

	public abstract void makeRequest(Request request)
			throws UnknownRequestTypeException;

}