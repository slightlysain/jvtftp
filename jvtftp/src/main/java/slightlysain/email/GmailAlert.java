package slightlysain.email;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import slightlysain.jvtftp.configuration.Configuration;

public class GmailAlert {
	private static Configuration config = null;
	private String message;
	private String subject;
	private String username;
	private String password;
	private String address;

	public static void setConfiguration(Configuration config) {
		GmailAlert.config = config;
	}
	
	public GmailAlert(String sub, String msg) {
		message = msg;
		subject = sub;
		if(null == config) {
			throw new NullPointerException("Static Configuration not set");
		}
		username = config.getProperty("slightlysain.email.username");
		password = config.getProperty("slightlysain.email.password");
		address = config.getProperty("slightlysain.email.address");
	}

	public void send() throws EmailException {
		SimpleEmail email = new SimpleEmail();
		email.setHostName("smtp.gmail.com");
		email.setAuthentication(username, password);
		email.setTLS(true);
		email.setSSL(true);
		email.setSslSmtpPort("465");
		email.addTo(address, address);
		email.setFrom(username, "Jvtftp Server");
		email.setSubject(subject);
		email.setMsg(message);
		email.send();
	}
	
	
	
	
}
