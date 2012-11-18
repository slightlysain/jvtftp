package slightlysain.jvtftp.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.net.tftp.TFTPErrorPacket;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.mock.CatchInstanceAction;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

import junit.framework.TestCase;

public class TestServerErrorHandler extends TestCase {

	/**
	 * See http://java.dzone.com/articles/unit-testing-asserting-line for
	 * details of checking logs
	 * 
	 * @throws UnknownHostException
	 */
	public void testNewError() throws UnknownHostException {
		Mockery context = new Mockery();

		InetAddress host = InetAddress.getByName("10.10.10.10");
		TFTPErrorPacket p = new TFTPErrorPacket(host, 2050, 1, "A test");

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory
				.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

		final CatchInstanceAction<LoggingEvent> catchLogEvent = new CatchInstanceAction<LoggingEvent>();
		final Appender append = context.mock(Appender.class);
		context.checking(new Expectations() {{
			//oneOf(append).getName(); will(returnValue("MOCK"));
			oneOf(append).doAppend(with(any(LoggingEvent.class))); will(catchLogEvent);
		}});
		root.addAppender(append);
		ServerErrorHandler h = new ServerErrorHandler(p);
		String message = catchLogEvent.getInstance().getFormattedMessage();
		assertTrue(message.contains("error"));
		context.assertIsSatisfied();
	}

}
