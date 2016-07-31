package com.yahoo.finance.configuration;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tausif
 *
 */
public enum ConfigManager {

	INSTANCE;
	
	private final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
	
	private AppConfig appConfig;
	
	private StringBuilder appBaseDirectory = new StringBuilder();
	
	private ConfigManager() {
	
		String defaultBaseDirectory = File.separatorChar + "opt" + File.separatorChar + "yahoo" + File.separatorChar + "conf";
		
		
		String dirKey = "CMD_CONFIG_BASE_DIR";
		
		if(System.getProperties().getProperty(dirKey) != null && !System.getProperties().getProperty(dirKey).trim().isEmpty()) {
			LOGGER.info("Found System properties " + System.getProperties().getProperty(dirKey).trim());
			appBaseDirectory.append(System.getProperties().getProperty(dirKey).trim());
		}
		else if(System.getenv().get(dirKey) != null && !System.getenv().get(dirKey).trim().isEmpty()) {
			LOGGER.info("Found Enviroment properties " + System.getenv().get(dirKey).trim());
			appBaseDirectory.append(System.getenv().get(dirKey).trim());
		} 
	
		if(!(appBaseDirectory == null || appBaseDirectory.length() == 0) && new File(appBaseDirectory.toString()).exists()) {
			LOGGER.info("Overriding default configuration with " + appBaseDirectory);
		}
		else {
			LOGGER.info("Using default base config directory");
			appBaseDirectory.append(defaultBaseDirectory);
		}
		
		String file = appBaseDirectory.toString() + File.separatorChar + "log4j.xml";
		DOMConfigurator.configure(file);
		
		appConfig = new AppConfig(appBaseDirectory.toString());
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public String getBaseDir() {
		return appBaseDirectory.toString();
	}
	
}
