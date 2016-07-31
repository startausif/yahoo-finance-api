package com.yahoo.finance.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.finance.configuration.ConfigManager;
import com.yahoo.finance.exceptions.AbstractException;
import com.yahoo.finance.exceptions.ApplicationException;

/**
 * @author tausif
 *
 */
public class CommonUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);
	
	private static Properties stockFileProperty = ConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("yahoo");
	
	public static boolean isNotNull(Object... args) {
		for (Object s : args) {
			if (s == null || (s != null && s.toString().equalsIgnoreCase(""))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(String str) {
		if (str == null || str.length() <= 0) {
			return true;
		}
		return false;
	}
	
	public static String readFile(){
		
		try(BufferedReader br = new BufferedReader(new FileReader(stockFileProperty.getProperty("stock.file").toString()))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
				if (br.readLine() != null) {
					sb.append("+");
				}
		        line = br.readLine();
		    }
		   return sb.toString();
		} catch (FileNotFoundException e){
			throw new ApplicationException("File does not exist");
		} catch (AbstractException e) {
			LOGGER.error("Error while reading file", e);
			throw new ApplicationException(e.getMessage());
		}		
		catch (Exception e) {
			LOGGER.error("Error while reading file", e);
			throw new ApplicationException(e.getMessage());
		}
	}
	
	/**
	 * Removes unwanted characters from file received from client
	 * @return
	 */
	public static String processString(String content){
		String[] contentArray = content.split("\n");
		StringBuilder sb = new StringBuilder();
		String output = null;
		for (String string : contentArray) {
		/*	if(CommonUtil.isEmpty(string) || string.contains("Content-") || string.contains("WebKit")){
				continue;
			}*/
			string = string.replace("\r", "");
			if(string.matches("[a-zA-Z.-]*") && !string.isEmpty()){
			sb.append(string);
			sb.append("+");
			}
		}
		if(sb.toString().endsWith("+"))
		{
			output = sb.toString().substring(0, sb.toString().length()-1);
		}
		return output;
		
	}
}
