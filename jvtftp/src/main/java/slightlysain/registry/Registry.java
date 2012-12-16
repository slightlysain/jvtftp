package slightlysain.registry;

import java.util.HashMap;
import java.util.Map;

public class Registry {
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public void put(String key, Object value) {
		map.put(key, value);
	}
	
	public Object get(String key) {
		return map.get(key);
	}
	
	public boolean contains(String key) {
		return map.containsKey(key);
	}
	
	
	
}
