package com.yahoo.finance.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds all properties of application.
 * Instance of this class should be accessed via AppConfigManager.
 * 
 * @author tausif
 *
 */
public class AppConfig {

	private final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
	
	private Properties properties;
	
	private final String appBaseConfigDir;
	
	public AppConfig(String configDir) {
		this.appBaseConfigDir = configDir;
		loadPropertiesFile();
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	private void loadPropertiesFile() {
		LOGGER.info("Loading properties");
		
		properties = new Properties();

		try {
			
			String file = appBaseConfigDir + File.separatorChar + "app.properties";
			File dir = new File(file);
			properties.load(new FileInputStream(dir));
			
		} catch (Exception e) {
			LOGGER.error("Error while loading properties", e);
		}
	}

	/**
	 * Returns properties with key/value pair for all keys that starts
	 * with <b>prefix</b> and separated by DOT (.). Returned properties contains keys with removed prefix.
	 * 
	 * @param prefix
	 * @return
	 */
	public Properties getPropertiesWithPrefix(String prefix) {
		
		Properties p = new Properties();
		
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String k = keys.nextElement().toString();
			
			String prefix2 = prefix + ".";
			if(k.startsWith(prefix2)) {
				
				String substring = k.substring(prefix2.length());
				if(substring.length() > 0)
					p.put(substring, properties.get(k));
			}
		}
		
		return p;
	}
	
	
}
