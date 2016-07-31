package com.yahoo.finance.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.finance.configuration.ConfigManager;
import com.yahoo.finance.exceptions.ApplicationException;
import com.yahoo.finance.interfaces.IYahooFinanceApi;
import com.yahoo.finance.route.client.HttpCaller;
import com.yahoo.finance.runner.MainVerticle;
import com.yahoo.finance.util.CommonUtil;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * 
 * @author tausif
 *
 */
public class FinanceApiImpl implements IYahooFinanceApi{

	private final Logger LOGGER = LoggerFactory.getLogger(FinanceApiImpl.class);
	private static Properties yahooFinanceUrl = ConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("yahoo");
	private RoutingContext context;
	private String stockFile;
	private String coloumnHeader;
	
	public FinanceApiImpl(RoutingContext context, String stockFile, String coloumnHeader){
		this.context = context;
		this.stockFile = stockFile;
		this.coloumnHeader = coloumnHeader;
	}


	
	@Override
	public void perform() {

		String csvHeader = CommonUtil.isNotNull(coloumnHeader) ? coloumnHeader : yahooFinanceUrl.getProperty("csv.format");
		
		if(!csvHeader.matches("[a-zA-Z]*")){
			throw new ApplicationException("Invalid coloumn header");
		}

		LOGGER.info("Initiating Yahoo finance API call with Thread ->." + Thread.currentThread().getName());
		try {

			String completeUrl = yahooFinanceUrl.getProperty("finance.api") + "?s=" + stockFile + "&" + "f=" + csvHeader;
			
			HashMap<String, String> financeAPIHeader = new HashMap<String, String>();
			financeAPIHeader.put("Content-Length", ""+100);
			financeAPIHeader.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; MotoE2(4G-LTE) Build/MPI24.65-39) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.81 Mobile Safari/537.36");

			HttpCaller httpCaller = new HttpCaller(MainVerticle.getVertx());
			httpCaller.callGet(completeUrl,  new Handler<HttpClientResponse>() {

						@Override
						public void handle(HttpClientResponse httpResponse) {

							processResponse(httpResponse);
						}

					});
		} catch (Exception e) {
			LOGGER.error("Exception while calling Yahoo finance API : ", e);
			throw new ApplicationException(e.getMessage());
		}

	}
	
	private void processResponse(HttpClientResponse httpResponse) {

		if (httpResponse.statusCode() != 200) {

			String error = String.format("Error in Yahoo Finance API with error code %s and error message %s",
					httpResponse.statusCode(), httpResponse.statusMessage());
			LOGGER.error(error);
			return;
		}
		httpResponse.bodyHandler(buffer -> {
			LOGGER.info("Yahoo finance API Result {}", buffer);
			String []processedOutput = processAPiOutput(buffer.toString());
			writToCsv(processedOutput);
			context.response().end("Csv file is wriiten at location " + yahooFinanceUrl.getProperty("csv.file"));
		});
	
	}

	private String[] processAPiOutput(String output) {

		String []outputArray = output.split("\n");
		String []modifiedOutput = new String[outputArray.length];
		int count = 0;
		for (String string : outputArray) {
			if(string.startsWith("N/A")){
				modifiedOutput[count++] = "-1";
				continue;
			}
			modifiedOutput[count++] = string;
		}
		return modifiedOutput;
	}

	private void writToCsv(String[] processedOutput) {

		try {
			
			FileWriter csvFile = new FileWriter(yahooFinanceUrl.getProperty("csv.file"));

			String newLine = "\n";
			
			for (String string : processedOutput) {
				csvFile.write(string + newLine);
			}
			csvFile.close();

		} catch (IOException e) {
        	LOGGER.error("Erro while writing to CSV",e);
        	throw new ApplicationException("Error while writing to file");
        }
		
	}
}
