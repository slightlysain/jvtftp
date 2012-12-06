package slightlysain.jvtftp.server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import slightlysain.jvtftp.request.Request;

public class ClientRegisterImpl implements ClientRegister {

	private Logger log = LoggerFactory.getLogger(getClass());
	private Map<InetAddress, Set<Integer>> clients;

	public ClientRegisterImpl() {
		clients = new HashMap<InetAddress, Set<Integer>>();
	}

	public synchronized boolean contains(InetAddress address, int port) {
		if (clients.containsKey(address)) {
			Set<Integer> ports = clients.get(address);
			return ports.contains(port);
		} else {
			return false;
		}
	}

	public synchronized boolean contains(Request req) {
		return this.contains(req.getAddress(), req.getPort());
	}

	public synchronized void register(InetAddress address, int port)
			throws ClientAlreadyRegisteredException {
		Set<Integer> ports;
		if (clients.containsKey(address)) {
			ports = clients.get(address);
		} else {
			ports = new HashSet<Integer>();
			clients.put(address, ports);
		}
		if (!ports.add(port)) {
			throw new ClientAlreadyRegisteredException();
		}
	}

	public synchronized void register(Request req) throws ClientAlreadyRegisteredException {
		this.register(req.getAddress(), req.getPort());
	}

	public synchronized void unregister(InetAddress address, int port)
			throws ClientNotRegisteredException {
		Set<Integer> ports;
		if (clients.containsKey(address)) {
			ports = clients.get(address);
			if (ports.contains(port)) {
				ports.remove(port);
				if (ports.isEmpty()) {
					clients.remove(address);
				}
			} else {
				throw new ClientNotRegisteredException();
			}
		} else {
			throw new ClientNotRegisteredException();
		}
	}

	public synchronized void unregister(Request req) throws ClientNotRegisteredException {
		this.unregister(req.getAddress(), req.getPort());
	}

}
