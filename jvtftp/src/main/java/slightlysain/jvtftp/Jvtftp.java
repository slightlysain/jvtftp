package slightlysain.jvtftp;

import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyScriptEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.net.tftp.TFTPPacketException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iterative.groovy.service.GroovyShellService;

import slightlysain.email.GmailAlert;
import slightlysain.jvtftp.configuration.Configuration;
import slightlysain.jvtftp.io.stream.StreamFactory;
import slightlysain.jvtftp.io.stream.StreamFactoryImpl;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactory;
import slightlysain.jvtftp.packetadapter.PacketAdapterFactoryImpl;
import slightlysain.jvtftp.request.handler.groovy.InvalidPriorityCommentException;
import slightlysain.jvtftp.request.handler.groovy.PriorityCommentNotFoundException;
import slightlysain.jvtftp.request.router.CouldNotLoadScriptException;
import slightlysain.jvtftp.request.router.RequestRouter;
import slightlysain.jvtftp.request.router.RequestRouterImpl;
import slightlysain.jvtftp.request.router.ScriptAlreadyLoadedException;
import slightlysain.jvtftp.server.ClientRegister;
import slightlysain.jvtftp.server.ClientRegisterImpl;
import slightlysain.jvtftp.server.Server;
import slightlysain.jvtftp.session.SessionFactory;
import slightlysain.jvtftp.session.SessionFactoryImpl;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import slightlysain.jvtftp.tftpadapter.TFTPAdapterFactory;
import slightlysain.jvtftp.tftpadapter.TFTPAdapterImpl;
import slightlysain.registry.Registry;

/**
 * Hello world!
 * 
 */
public class Jvtftp {
	private static final String PORT_KEY = "slightlysain.jvtftp.port";
	private static final String STARTSCRIPTS_KEY = "slightlysain.jvtftp.startscripts";
	private static final String OUTGOINGFILES_KEY = "slightlysain.jvtftp.outgoingfiles";
	private static final String INCOMINGFILES_KEY = "slightlysain.jvtftp.incomingfiles";
	private static final String CONFIG_KEY = "slightlysain.jvtftp.config";
	private static final String SCRIPTDIR_KEY = "slightlysain.jvtftp.scriptsdir";
	
	static int DEFAULT_PORT = 2269;
	static final String DEFAULT_CONFIG = "/home/harry/git/jvtftp/jvtftp/config/config.properties";

	static Logger log = LoggerFactory.getLogger(Jvtftp.class);
	static Properties sysproperties = System.getProperties();
	static Properties configproperties;
	static boolean hasConfig = false;
	static Configuration configuration = new Configuration();

	public static void main(String[] args) {

		String config = System.getProperty(CONFIG_KEY);
		if (null != config) {
			loadConfiguration(config);
		} else {
			System.out
					.println("No config file specified using default configuration");
			loadConfiguration(DEFAULT_CONFIG);
		}
		
		GmailAlert.setConfiguration(configuration);
		// setup script roots
		String roots[] = null;
		String dir = configuration.getProperty(SCRIPTDIR_KEY);
		if (null != dir) {
			System.out.println("Script dir:" + dir);
			roots = new String[] { dir };
		} else {
			System.out.println("Scripts directory not set");
			System.exit(-1);
		}
		// create script engine
		GroovyScriptEngine engine = null;
		try {
			engine = createScriptEngine(roots);
		} catch (IOException e1) {
			log.error("could not create script engine");
			System.exit(-1);
		}

		String incomingDir = configuration.getProperty(INCOMINGFILES_KEY);
		String outgoingDir;
		StreamFactory streamFactory = null;
		if (null != incomingDir) {
			outgoingDir = configuration.getProperty(OUTGOINGFILES_KEY);
			if (null != outgoingDir) {
				streamFactory = new StreamFactoryImpl(incomingDir, outgoingDir);
			} else {
				System.out.println("Could not get outgoing files directory");
				System.exit(-1);
			}
		} else {
			System.out.println("Could not get incoming files directory");
			System.exit(-1);
		}
		// setup request router
		ExecutorService executor = Executors.newCachedThreadPool();
		TFTPAdapter serverTFTPAdapter = new TFTPAdapterImpl();
		PacketAdapterFactory packetAdapterFactory = new PacketAdapterFactoryImpl();
		TFTPAdapterFactory tFTPAdapterFactory = new TFTPAdapterFactory();
		SessionFactory sessionFactory = new SessionFactoryImpl(
				packetAdapterFactory, tFTPAdapterFactory);
		Registry registry = new Registry();
		RequestRouter requestRouter = new RequestRouterImpl(engine,
				sessionFactory, streamFactory, registry, executor,
				configuration);
		String scripts = configuration.getProperty(STARTSCRIPTS_KEY);
		if (null != scripts && scripts.length() > 0) {
			String[] allscripts = scripts.split(",");
			for (String script : allscripts) {
				loadScript(requestRouter, script.trim());
			}
		} else {
			System.out.println("No startup scripts specified");
		}

		int port = DEFAULT_PORT;
		String portstring = configuration.getProperty(PORT_KEY);
		if (null != portstring) {
			port = Integer.parseInt(portstring);
		} else {
			System.out.println("using default port");
		}

		ClientRegister clientRegister = new ClientRegisterImpl();

		Map<String, Object> bindings = new HashMap<String, Object>();
		bindings.put("requestRouter", requestRouter);
		bindings.put("registry", registry);
		bindings.put("config", configuration);
		// start groovy shell
		GroovyShellService gss = new GroovyShellService(bindings, 5000);
		gss.launchInBackground();
		Server server = new Server(port, requestRouter, executor,
				serverTFTPAdapter, clientRegister);
		try {
			server.start();
		} catch (SocketException e) {
			log.error("SocketException when starting server", e);
			// e.printStackTrace();
		} catch (IOException e) {
			log.error("IOException when starting server", e);
		} catch (TFTPPacketException e) {
			log.error("TFTPPacketException when starting server", e);
		}
		System.out.println("Server ended");
	}

	private static void loadConfiguration(String config) {
		configproperties = new Properties();
		hasConfig = true;
		try {
			configuration.load(config);
		} catch (FileNotFoundException e) {
			System.out.println("Could not open config file");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Could not read config file");
			System.exit(-1);
		}
	}

	private static void loadScript(RequestRouter router, String name) {
		try {
			router.loadScript(name);
		} catch (ScriptAlreadyLoadedException e1) {
			System.out.println(name + " already loaded");
			System.exit(-1);
		} catch (CouldNotLoadScriptException e1) {
			System.out.println("Could not load " + name);
			System.exit(-1);
		} catch (PriorityCommentNotFoundException e) {
			System.out.println(name + " priority comment not found");
			System.exit(-1);
		} catch (InvalidPriorityCommentException e) {
			System.out.println(name + " invalid priority comment");
			System.exit(-1);
		} catch (FileNotFoundException e) {
			System.out.println("could not find " + name);
			System.exit(-1);
		}
	}

	private static GroovyScriptEngine createScriptEngine(String[] roots)
			throws IOException {
		CompilerConfiguration cc = new CompilerConfiguration();
		cc.setScriptBaseClass("slightlysain.jvtftp.request.handler.groovy.AbstractScript");
		GroovyClassLoader gcl = new GroovyClassLoader(
				ClassLoader.getSystemClassLoader(), cc);
		GroovyScriptEngine engine = new GroovyScriptEngine(roots, gcl);
		return engine;
	}
}
