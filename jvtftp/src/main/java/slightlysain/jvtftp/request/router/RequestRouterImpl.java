package slightlysain.jvtftp.request.router;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import slightlysain.jvtftp.request.RequestAbstract;

import co.eke.tftp.ScriptCommand;
import co.eke.tftp.commands.RequestCommand;
import co.eke.tftp.exceptions.CommandExistsException;
import co.eke.tftp.exceptions.CommandPriorityNotSet;
import co.eke.tftp.exceptions.CouldNotLoadScriptException;
import co.eke.tftp.exceptions.ScriptAlreadyLoadedException;
import co.eke.tftp.exceptions.ScriptNotAlreadyLoadedException;
import co.eke.tftp.exceptions.UnknownRequestTypeException;

public class RequestRouterImpl implements RequestRouter {
	private static RequestRouter uniqueInstance = new RequestRouterImpl();
	private RequestChain commandChain;
	private Map<String, RequestCommand> loadedScripts = new ConcurrentHashMap<String, RequestCommand>();
	private static Logger LOG = Logger.getLogger(RequestRouterImpl.class);

	/*------------------------------------constructor--------------------------------*/
	private RequestRouterImpl() {
		commandChain = new RequestChain();
	}

	/*------------------------------------private--------------------------------*/
	private void addLink(RequestCommand cmd) throws CommandExistsException {	
		try {
			commandChain.addLink(cmd);
		} catch (CommandPriorityNotSet e) {
			LOG.error("command priority not set", e);
		}	
	}

	private  void removeLink(RequestCommand cmd) {
		try {
			commandChain.removeLink(cmd);
		} catch (CommandPriorityNotSet e) {
			LOG.debug("Remove command priority not set", e);
		}
	}

	private void passToChain(RequestAbstract r) {
		commandChain.getChainHead().execute(r);
	}

	private void addScriptToLoadedList(String script, RequestCommand cmd) {
		loadedScripts.put(script, cmd);
	}

	private void removeScriptFromLoadedList(String script) {
		loadedScripts.remove(script);
	}

	private RequestCommand createCommand(String script) throws CouldNotLoadScriptException {
		RequestCommand cmd = null;
		try {
			cmd = new ScriptCommand(script, this);
		} catch (CommandPriorityNotSet e) {
			LOG.error("Script command priority not set", e);
		}
		if(null == cmd) {
			LOG.error("Could not create command:" + script);
			throw new CouldNotLoadScriptException();
		}
		return cmd;
	}

	/*--------------------------------------------public----------------------------------*/
	/* (non-Javadoc)
	 * @see slighltysain.jvtftp.request.RequestRouter#scriptIsLoaded(java.lang.String)
	 */
	public boolean isScriptLoaded(String script) {
		return loadedScripts.containsKey(script);
	}

	public void loadScript(String script) throws ScriptAlreadyLoadedException, CouldNotLoadScriptException {
		if(isScriptLoaded(script)) {
			LOG.error("Script is already loaded:" + script);
			throw new ScriptAlreadyLoadedException();
		} else {
			RequestCommand cmd = createCommand(script);
			try {
				addLink(cmd);
			} catch (CommandExistsException e) {
				LOG.error("command exists exception in load script", e);
				throw new ScriptAlreadyLoadedException();
			}
			addScriptToLoadedList(script, cmd);
			LOG.debug("Loaded script:" + script);
		} 
	}

	public void unloadScript(String script) throws ScriptNotAlreadyLoadedException {
		if(isScriptLoaded(script)) {
			RequestCommand cmd = loadedScripts.get(script);
			removeLink(cmd);
			removeScriptFromLoadedList(script);
		} else {
			throw new ScriptNotAlreadyLoadedException();
		}
		LOG.info("unload script " + script);
	}

	public void makeRequest(RequestAbstract r) throws UnknownRequestTypeException {
		LOG.debug("requested file " + r.getFile());
		passToChain(r);
	}

	/*---------------------------------public-get-instance---------------------------------*/
	public static RequestRouter getInstance() {
		return uniqueInstance;
	}

}
