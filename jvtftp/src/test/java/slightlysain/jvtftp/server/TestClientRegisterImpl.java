package slightlysain.jvtftp.server;

import static org.junit.Assert.*;

import java.net.InetAddress;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import slightlysain.jvtftp.request.Request;

import junit.framework.TestCase;

public class TestClientRegisterImpl {

	ClientRegister register;
	Mockery context;
	InetAddress host;
	int port;

	@Before
	public void setUp() throws Exception {
		register = new ClientRegisterImpl();
		context = new Mockery();
		host = InetAddress.getByName("10.10.10.10");
		port = 5000;
	}

	/*
	 * testREgister1 testregister2 testunregister(req) testUnregister(host,port)
	 * testREgister1 (alreadyRegistered) testregister2 (alreadyRegistered)
	 * testunregister(req) (notRegisterd) testUnregister(host,port)
	 * (notRegistered) testContains(host,port) testcontains(req)
	 * testContains(host,port) (with entries) testcontains(req) (with entries)
	 * client diffrent port register
	 * 
	 * add, contains, remove, contains
	 */
	@Test
	public void testRegister() throws ClientAlreadyRegisteredException {
		register.register(host, port);
		context.assertIsSatisfied();
	}

	@Test
	public void testRegisterWithRequest()
			throws ClientAlreadyRegisteredException {
		final Request req = context.mock(Request.class);
		context.checking(new Expectations() {
			{
				oneOf(req).getAddress();
				will(returnValue(host));
				oneOf(req).getPort();
				will(returnValue(port));
			}
		});
		register.register(req);
		context.assertIsSatisfied();
	}

	@Test
	public void testUnregister() throws ClientAlreadyRegisteredException,
			ClientNotRegisteredException {
		register.register(host, port);
		register.unregister(host, port);
		context.assertIsSatisfied();
	}
	@Test
	public void testUnregisterWithRequest()
			throws ClientAlreadyRegisteredException,
			ClientNotRegisteredException {
		final Request req = context.mock(Request.class);
		context.checking(new Expectations() {
			{
				atLeast(2).of(req).getAddress();
				will(returnValue(host));
				atLeast(2).of(req).getPort();
				will(returnValue(port));
			}
		});
		register.register(req);
		register.unregister(req);
		context.assertIsSatisfied();
	}
	@Test
	public void testDoubleRegister() throws ClientAlreadyRegisteredException {
		register.register(host, port);
		boolean exception = false;
		try {
			register.register(host, port);
		} catch (ClientAlreadyRegisteredException e) {
			exception = true;
		}
		assertTrue(exception);
		context.assertIsSatisfied();
	}
	@Test
	public void testDoubleRegisterUsingRequest()
			throws ClientAlreadyRegisteredException {
		final Request req = context.mock(Request.class);
		context.checking(new Expectations() {
			{
				atLeast(2).of(req).getAddress();
				will(returnValue(host));
				atLeast(2).of(req).getPort();
				will(returnValue(port));
			}
		});
		register.register(req);
		boolean exception = false;
		try {
			register.register(req);
		} catch (ClientAlreadyRegisteredException e) {
			exception = true;
		}
		assertTrue(exception);
		context.assertIsSatisfied();
	}
	@Test
	public void testNoClientUnregister() {
		boolean exception = false;
		try {
			register.unregister(host, port);
		} catch (ClientNotRegisteredException e) {
			exception = true;
		}
		assertTrue(exception);
		context.assertIsSatisfied();
	}
	@Test
	public void testNoClientUnregisterUsingRequest() {
		final Request req = context.mock(Request.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(req).getAddress();
				will(returnValue(host));
				atLeast(1).of(req).getPort();
				will(returnValue(port));
			}
		});
		boolean exception = false;
		try {
			register.unregister(req);
		} catch (ClientNotRegisteredException e) {
			exception = true;
		}
		assertTrue(exception);
		context.assertIsSatisfied();
	}
	@Test
	public void testFalseContains() {
		assertFalse(register.contains(host, port));
		context.assertIsSatisfied();
	}
	@Test
	public void testFalseContainsUsingRequest() {
		final Request req = context.mock(Request.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(req).getAddress();
				will(returnValue(host));
				atLeast(1).of(req).getPort();
				will(returnValue(port));
			}
		});
		assertFalse(register.contains(req));
		context.assertIsSatisfied();
	}
	@Test
	public void testTrueContains() throws ClientAlreadyRegisteredException {
		register.register(host, port);
		assertTrue(register.contains(host, port));
		context.assertIsSatisfied();
	}
	@Test
	public void testTrueContainsWithRequest()
			throws ClientAlreadyRegisteredException {
		final Request req = context.mock(Request.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(req).getAddress();
				will(returnValue(host));
				atLeast(1).of(req).getPort();
				will(returnValue(port));
			}
		});
		register.register(req);
		assertTrue(register.contains(req));
		context.assertIsSatisfied();
	}
	@Test
	public void testAdd_Contains_Remove_Contains()
			throws ClientAlreadyRegisteredException,
			ClientNotRegisteredException {
		register.register(host, port);
		assertTrue(register.contains(host, port));
		register.unregister(host, port);
		assertFalse(register.contains(host, port));
		context.assertIsSatisfied();
	}
	@Test
	public void testAdd_Contains_Remove_ContainsWithRequest()
			throws ClientAlreadyRegisteredException,
			ClientNotRegisteredException {
		final Request req = context.mock(Request.class);
		context.checking(new Expectations() {
			{
				atLeast(4).of(req).getAddress();
				will(returnValue(host));
				atLeast(4).of(req).getPort();
				will(returnValue(port));
			}
		});
		register.register(req);
		assertTrue(register.contains(req));
		register.unregister(req);
		assertFalse(register.contains(req));
		context.assertIsSatisfied();
	}
	@Test
	public void testAddAddRemove() throws ClientAlreadyRegisteredException,
			ClientNotRegisteredException {
		register.register(host, port);
		boolean exception = false;
		try {
			register.register(host, port);
		} catch (ClientAlreadyRegisteredException e) {
			exception = true;
		}
		assertTrue(exception);
		register.unregister(host, port);
		context.assertIsSatisfied();
	}
	@Test
	public void testAddAddRemoveWithRequest()
			throws ClientAlreadyRegisteredException,
			ClientNotRegisteredException {
		final Request req = context.mock(Request.class);
		context.checking(new Expectations() {
			{
				atLeast(3).of(req).getAddress();
				will(returnValue(host));
				atLeast(3).of(req).getPort();
				will(returnValue(port));
			}
		});
		register.register(req);
		boolean exception = false;
		try {
			register.register(req);
		} catch (ClientAlreadyRegisteredException e) {
			exception = true;
		}
		assertTrue(exception);
		register.unregister(req);
		context.assertIsSatisfied();
	}

}
