package com.yahoo.finance.route.client;

import java.util.HashMap;
import java.util.Map.Entry;

import com.yahoo.finance.exceptions.AbstractException;
import com.yahoo.finance.util.CommonUtil;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;

public class HttpCaller {

	private final Vertx vertx;
	
	public HttpCaller(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public void callGet(final String url, final Handler<HttpClientResponse> responseHandler) {
		
		final HttpClientOptions clientOptions = new HttpClientOptions();
		clientOptions.setSsl(false);
		HttpClient httpClient = vertx.createHttpClient(clientOptions);
	    HttpClientRequest request = httpClient.requestAbs(HttpMethod.GET, url, responseHandler);
		request.putHeader("User-Agent",
				"Mozilla/5.0 (Linux; Android 6.0.1; MotoG3 Build/MPI24.107-55) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.81 Mobile Safari/537.36");
		request.end();
	}

	public void callPost(final String url, Buffer data, HashMap<String, String> authHeader,
			final Handler<HttpClientResponse> responseHandler) throws AbstractException{

		final HttpClientOptions clientOptions = new HttpClientOptions();
		clientOptions.setSsl(false);
		clientOptions.setConnectTimeout(5000);
		final HttpClient httpClient = vertx.createHttpClient(clientOptions);
		HttpClientRequest request = httpClient.postAbs(url, responseHandler);
		request.setChunked(true);
		if(CommonUtil.isNotNull(authHeader))
		for (Entry<String, String> entry : authHeader.entrySet()) {
			request.putHeader(entry.getKey(), entry.getValue());
		}
		if(CommonUtil.isNotNull(data))
		request.write(data);
		request.end();
	}
	
}

