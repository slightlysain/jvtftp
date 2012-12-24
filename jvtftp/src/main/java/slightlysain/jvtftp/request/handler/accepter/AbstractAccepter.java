package slightlysain.jvtftp.request.handler.accepter;

import java.io.InputStream;
import java.io.OutputStream;

import slightlysain.jvtftp.request.Request;
import slightlysain.jvtftp.request.handler.RequestHandler;
import slightlysain.jvtftp.request.handler.groovy.RequestHandlerBinding;
import slightlysain.jvtftp.session.SessionController;
import slightlysain.jvtftp.session.SessionFactory;

public class AbstractAccepter {
	private Request request;
	private SessionFactory sessionFactory;

	public AbstractAccepter(RequestHandlerBinding binding) {
		request = binding.getRequest();
		sessionFactory = binding.getSessionFactory();	
	}

	protected SessionController createController(Request request) {
		SessionController control = sessionFactory.createController(request);
		return control;
	}

	protected void accept(InputStream input) {
		SessionController control = createController(request);
		control.send(input);
	}

	protected void accept(OutputStream output) {
		SessionController control = createController(request);
		control.recieve(output);
	}

}