package slightlysain.jvtftp;

import groovy.lang.GroovyClassLoader;
import groovy.util.GroovyScriptEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.net.tftp.TFTPPacketException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import slightlysain.jvtftp.tftpadapter.TFTPAdapterImpl;

/**
 * Hello world!
 * 
 */
public class Jvtftp {
	static int PORT = 2269;

	static Logger log = LoggerFactory.getLogger(Jvtftp.class);
	static Properties sysproperties = System.getProperties();
	static Properties configproperties;
	static boolean hasConfig = false;

	public static String getProperty(String str) {
		if (hasConfig) {
			return sysproperties.getProperty(str,
					configproperties.getProperty(str));
		} else {
			return sysproperties.getProperty(str);
		}
	}

	public static void main(String[] args) {
		int i = 0;
		String config = System.getProperty("jvtftp.config");
		if (null != config) {
			configproperties = new Properties();
			hasConfig = true;
			try {
				configproperties.load(new FileInputStream(config));
			} catch (FileNotFoundException e) {
				System.out.println("Could not open config file");
				System.exit(-1);
			} catch (IOException e) {
				System.out.println("Could not read config file");
				System.exit(-1);
			}
		}
		String roots[] = { "/home/harry/eclipse-workspace/Script Test/scripts" };
		String dir = getProperty("jvtftp.scriptdir");
		if (null != dir) {
			System.out.println("Script dir:" + dir);
			roots = new String[] { dir };
		}
		GroovyScriptEngine engine = null;
		try {
			engine = createScriptEngine(roots);
		} catch (IOException e1) {
			log.error("could not create script engine");
			System.exit(-1);
		}
		ExecutorService executor = Executors.newCachedThreadPool();
		TFTPAdapter serverTFTPAdapter = new TFTPAdapterImpl();
		PacketAdapterFactory adapterFactory = new PacketAdapterFactoryImpl();
		SessionFactory sessionFactory = new SessionFactoryImpl(adapterFactory);
		RequestRouter requestRouter = new RequestRouterImpl(engine,
				sessionFactory);
		String scripts = getProperty("jvtftp.scripts");
		if (null != scripts) {
			String[] allscripts = scripts.split(":");
			for (String s : allscripts) {
				System.out.println("script:" + s);
			}
		} else {
			loadScript(requestRouter, "all.groovy");
			loadScript(requestRouter, "donothing.groovy");
		}

		ClientRegister clientRegister = new ClientRegisterImpl();
		Server server = new Server(PORT, requestRouter, executor,
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
