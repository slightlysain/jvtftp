package slighltysain.jvtftp.request.router;

import slighltysain.jvtftp.request.RequestImpl;
import slighltysain.jvtftp.request.UnknownRequestTypeException;

public interface RequestRouter {

	/*--------------------------------------------public----------------------------------*/
	public abstract boolean isScriptLoaded(String script);

	public abstract void loadScript(String script)
			throws ScriptAlreadyLoadedException, CouldNotLoadScriptException;

	public abstract void unloadScript(String script)
			throws ScriptNotAlreadyLoadedException;

	public abstract void makeRequest(RequestImpl r)
			throws UnknownRequestTypeException;

}