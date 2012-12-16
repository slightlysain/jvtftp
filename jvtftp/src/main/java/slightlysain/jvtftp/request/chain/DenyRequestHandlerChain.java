package slightlysain.jvtftp.request.chain;

import java.util.Set;

import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.RequestHandler;
import slightlysain.jvtftp.session.SessionController;
import slightlysain.jvtftp.session.SessionFactory;
import slightlysain.jvtftp.tftpadapter.TFTPAdapter;
import slightlysain.jvtftp.tftpadapter.TFTPAdapterImpl;

public class DenyRequestHandlerChain implements RequestHandlerChain {

	private SessionFactory sessionFactory;
	private Logger log = LoggerFactory.getLogger(getClass());

	public DenyRequestHandlerChain(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void set(Set<RequestHandler> handlerSet) {
		log.error("set() called on deny request handler");
	}

	public void setNextChain(RequestHandlerChain handlerChain) {
		log.error("setNextChain() called on deny request handler");
	}

	private SessionController createController(Request request) {
		TFTPAdapter tftpadapter = new TFTPAdapterImpl();
		SessionController control = sessionFactory.createController(request,
				tftpadapter);
		return control;
	}

	public void execute(Request request) {
		SessionController control = createController(request);
		if (request.isRead()) {
			control.error(TFTPErrorPacket.FILE_NOT_FOUND,
					"The file " + request.getFilename() + " is unavailable");
		} else if (request.isWrite()) {
			control.error(TFTPErrorPacket.ACCESS_VIOLATION, "The file "
					+ request.getFilename() + " can not be written");
		}
	}

}
