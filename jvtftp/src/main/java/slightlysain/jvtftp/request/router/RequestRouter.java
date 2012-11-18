package slightlysain.jvtftp.request.router;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.RequestAbstract;
import slightlysain.jvtftp.request.UnknownRequestTypeException;

public interface RequestRouter {

	/*--------------------------------------------public----------------------------------*/
	public abstract boolean isScriptLoaded(String script);

	public abstract void loadScript(String script)
			throws ScriptAlreadyLoadedException, CouldNotLoadScriptException;

	public abstract void unloadScript(String script)
			throws ScriptNotAlreadyLoadedException;

	public abstract void makeRequest(Request request)
			throws UnknownRequestTypeException;

}