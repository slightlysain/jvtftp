package slightlysain.jvtftp.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	private boolean hasConfig = false;
	private Properties sysproperties = System.getProperties();
	private Properties configproperties;

	public Configuration() {}
	
	public Configuration(String filename) throws FileNotFoundException, IOException {
		load(filename);
	}

	public void load(String config) throws FileNotFoundException,
			IOException {
		configproperties = new Properties();
		hasConfig = true;
		configproperties.load(new FileInputStream(config));

	}

	public String getProperty(String str) {
		if (hasConfig) {
			return sysproperties.getProperty(str,
					configproperties.getProperty(str));
		} else {
			return sysproperties.getProperty(str);
		}
	}
	
	public boolean contains(String s) {
		return (sysproperties.containsKey(s) || configproperties.containsKey(s));
	}
}
