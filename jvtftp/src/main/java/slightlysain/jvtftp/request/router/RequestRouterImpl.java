package slightlysain.jvtftp.request.router;

import groovy.util.GroovyScriptEngine;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.configuration.Configuration;
import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.RequestAbstract;
import slightlysain.jvtftp.request.UnknownRequestTypeException;
import slightlysain.jvtftp.request.chain.DenyRequestHandlerChain;
import slightlysain.jvtftp.request.chain.RequestHandlerChain;
import slightlysain.jvtftp.request.chain.RequestHandlerChainImpl;
import slightlysain.jvtftp.request.handler.NoPriorityException;
import slightlysain.jvtftp.request.handler.RequestHandler;
import slightlysain.jvtftp.request.handler.RequestHandlerPriority;
import slightlysain.jvtftp.request.handler.groovy.GroovyRequestHandler;
import slightlysain.jvtftp.request.handler.groovy.GroovyScriptFile;
import slightlysain.jvtftp.request.handler.groovy.GroovyScriptFileImpl;
import slightlysain.jvtftp.request.handler.groovy.InvalidPriorityCommentException;
import slightlysain.jvtftp.request.handler.groovy.PriorityCommentNotFoundException;
import slightlysain.jvtftp.session.SessionController;
import slightlysain.jvtftp.session.SessionControllerImpl;
import slightlysain.jvtftp.session.SessionFactory;
import slightlysain.registry.Registry;

public class RequestRouterImpl implements RequestRouter {

	// private static RequestRouter uniqueInstance = new RequestRouterImpl();
	/*---------------------------------public-get-instance---------------------------------*/
	// public static RequestRouter getInstance() {
	// return uniqueInstance;
	// }

	private Map<RequestHandlerPriority, RequestHandlerChain> handlerChains;
	private Map<RequestHandlerPriority, Set<RequestHandler>> handlerSets;
	private RequestHandlerChain headChain;
	private Map<String, RequestHandler> loadedScripts = new ConcurrentHashMap<String, RequestHandler>();
	private GroovyScriptEngine scriptengine;
	private SessionFactory sessionFactory;
	private StreamFactory streamFactory;
	private static Logger log = LoggerFactory
			.getLogger(RequestRouterImpl.class);
	private Registry registry;
	private ExecutorService executorService;
	private Configuration configuration;

	/*------------------------------------constructor--------------------------------*/
	public RequestRouterImpl(GroovyScriptEngine scriptengine,
			SessionFactory sessionFactory, StreamFactory streamFactory,
			Registry registry, ExecutorService executorService,
			Configuration configuration) {
		this.executorService = executorService;
		this.scriptengine = scriptengine;
		this.sessionFactory = sessionFactory;
		this.streamFactory = streamFactory;
		this.registry = registry;
		this.configuration = configuration;
		handlerChains = new ConcurrentHashMap<RequestHandlerPriority, RequestHandlerChain>();
		handlerSets = new ConcurrentHashMap<RequestHandlerPriority, Set<RequestHandler>>();
		RequestHandlerPriority[] priorities = RequestHandlerPriority.values();
		RequestHandlerChain last = null;
		for (int i = priorities.length; i > 0; i--) {
			RequestHandlerPriority p = priorities[i - 1];
			RequestHandlerChain chain = new RequestHandlerChainImpl();
			if (null != last) {
				last.setNextChain(chain);
			} else {
				headChain = chain;
			}
			last = chain;
			Set<RequestHandler> set = new CopyOnWriteArraySet<RequestHandler>();
			handlerSets.put(p, set);
			chain.set(set);
			if (p == RequestHandlerPriority.LOW_COMMAND) {
				chain.setNextChain(new DenyRequestHandlerChain(sessionFactory));
			}
			handlerChains.put(p, chain);
		}
	}

	/*------------------------------------private--------------------------------*/
	private Set<RequestHandler> getPrioritySet(RequestHandler handler)
			throws NoPriorityException {
		RequestHandlerPriority priority;
		priority = handler.getPriority();
		return handlerSets.get(priority);
	}

	private void addLink(RequestHandler handler) throws NoPriorityException {
		getPrioritySet(handler).add(handler);
	}

	private void removeLink(RequestHandler handler) throws NoPriorityException {
		getPrioritySet(handler).remove(handler);
	}

	private RequestHandler createHandler(String script)
			throws PriorityCommentNotFoundException,
			InvalidPriorityCommentException, FileNotFoundException {
		GroovyScriptFile scriptFile = new GroovyScriptFileImpl(scriptengine,
				script);
		return new GroovyRequestHandler(scriptFile, sessionFactory, this,
				streamFactory, registry, executorService, configuration);
	}

	/*--------------------------------------------public----------------------------------*/
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * slighltysain.jvtftp.request.RequestRouter#scriptIsLoaded(java.lang.String
	 * )
	 */
	public boolean isScriptLoaded(String script) {
		return loadedScripts.containsKey(script);
	}

	public void loadScript(String script) throws ScriptAlreadyLoadedException,
			CouldNotLoadScriptException, PriorityCommentNotFoundException,
			InvalidPriorityCommentException, FileNotFoundException {
		if (isScriptLoaded(script)) {
			log.error("Script is already loaded:" + script);
			throw new ScriptAlreadyLoadedException();
		} else {
			RequestHandler handler = createHandler(script);
			try {
				addLink(handler);
			} catch (NoPriorityException e) {
				log.error("script:" + script + " has no priority");
				throw new CouldNotLoadScriptException("script has no prioity");
			}
			loadedScripts.put(script, handler);
			log.debug("Loaded script:" + script);
		}
	}

	public void unloadScript(String script)
			throws ScriptNotAlreadyLoadedException,
			CouldNotRemoveScriptException {
		if (isScriptLoaded(script)) {
			RequestHandler handler = loadedScripts.get(script);
			try {
				removeLink(handler);
				loadedScripts.remove(script);
				log.info("unload script " + script);
			} catch (NoPriorityException e) {
				log.error("script:" + script + " has no priority");
				throw new CouldNotRemoveScriptException();
			}
		} else {
			throw new ScriptNotAlreadyLoadedException();
		}
	}

	public void makeRequest(Request request) throws UnknownRequestTypeException {
		log.debug("requested file " + request.getFilename());
		headChain.execute(request);
	}
}
