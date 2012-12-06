package slightlysain.jvtftp.request.handler.groovy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.util.ResourceException;
import groovy.util.ScriptException;
import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.AbstractRequestHandler;
import slightlysain.jvtftp.request.handler.NoPriorityException;
import slightlysain.jvtftp.request.router.CouldNotRemoveScriptException;
import slightlysain.jvtftp.request.router.RequestHandlerPriority;
import slightlysain.jvtftp.request.router.RequestRouter;
import slightlysain.jvtftp.request.router.ScriptNotAlreadyLoadedException;
import slightlysain.jvtftp.session.Chunker;
import slightlysain.jvtftp.session.SessionController;
import slightlysain.jvtftp.session.SessionFactory;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import slightlysain.jvtftp.tftpadapter.TFTPAdapterImpl;

public class GroovyRequestHandler extends AbstractRequestHandler {
	private GroovyScriptFile scriptFile;
	private SessionFactory factory;
	private RequestRouter requestRouter;
	private Logger log = LoggerFactory.getLogger(getClass());

	public GroovyRequestHandler(GroovyScriptFile script,
			SessionFactory controller, RequestRouter requestRouter) {
		this.scriptFile = script;
		this.factory = controller;
		this.requestRouter = requestRouter;
	}

	public void accept(InputStream input, Request request) {
		SessionController control = createController(request);
		control.send(input);
	}

	private SessionController createController(Request request) {
		TFTPAdapter tftpadapter = new TFTPAdapterImpl();
		SessionController control = factory.createController(request,
				tftpadapter);
		return control;
	}

	public void accept(OutputStream output, Request request) {
		SessionController control = createController(request);
		control.recieve(output);
	}

	public void deny(Request request) {
		SessionController control = createController(request);
		control.error(TFTPErrorPacket.FILE_NOT_FOUND,
				"The file " + request.getFilename() + " is unavailable");
	}

	public void deny(int error, Request request) {
		SessionController control = createController(request);
		control.error(error, "This file is presently unavailable");
	}

	public void deny(Request request, int error, String message) {
		SessionController control = createController(request);
		control.error(error, message);
	}

	private void unload(Request request) {
		try {
			requestRouter.unloadScript(scriptFile.getName());
		} catch (ScriptNotAlreadyLoadedException e1) {
			log.error("Problem unloading, script not loaded", e1);
		} catch (CouldNotRemoveScriptException e1) {
			log.error("Problem unloading script ", e1);
		}
		skip(request);
	}

	@Override
	public void execute(Request request) {
		RequestHandlerBinding binding = new RequestHandlerBinding(request,
				this, null);
		try {
			scriptFile.run(binding);
		} catch (ResourceException e) {
			log.error("Problem running script:" + scriptFile.getName(), e);
			unload(request);
			return;
		} catch (ScriptException e) {
			log.error("Problem running script:" + scriptFile.getName(), e);
			unload(request);
			return;
		}
		if (!binding.hasHandled()) {
			super.execute(request);
		}
	}

	public void skip(Request request) {
		super.execute(request);
	}

	public RequestHandlerPriority getPriority() throws NoPriorityException {
		return scriptFile.getPriority();
	}

}
