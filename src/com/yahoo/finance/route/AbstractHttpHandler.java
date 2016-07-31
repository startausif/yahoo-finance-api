package com.yahoo.finance.route;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.finance.exceptions.AbstractException;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;

/**
 * Generic class to handle any kind of route.
 * 
 * 
 * @author tausif
 *
 * @param <R>
 * @param <T>
 */
public abstract class AbstractHttpHandler implements Handler<RoutingContext> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpHandler.class);

	private RoutingContext context;
	
	private Route currentRoute;

	@Override
	public void handle(RoutingContext context) {
		LOGGER.info("Received request to handle");
		this.context = context;
		currentRoute = context.currentRoute();
		context.request().bodyHandler(new HttpBufferHandler());
	}

	protected RoutingContext getRoutingContext() {
		return context;
	}

	private class HttpBufferHandler implements Handler<Buffer> {

		@Override
		public void handle(Buffer buffer) {
			try {
				Map<String, String> headers = getHeaders();
				for (Entry<String, String> e : headers.entrySet()) {
					context.response().putHeader(e.getKey(), e.getValue());
				}
				Object r = readBufferedRequest(buffer);
				handleRequest(r);
			} catch (AbstractException e) {
				context.response().end(e.toString());
				throw e;
				
			}
		}
	}

	protected abstract Object readBufferedRequest(Buffer buffer) throws AbstractException;

	protected abstract Object formatResponse(Object r) throws AbstractException;

	protected abstract void handleRequest(Object r) throws AbstractException;

	/*
	 * Process the headers passed in the API request.
	 */
	protected abstract Map<String, String> getHeaders();

	public Route getCurrentRoute() {
		return currentRoute;
	}
	

}
