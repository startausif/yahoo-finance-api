package com.yahoo.finance.runner;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.finance.common.annotation.UrlPath;
import com.yahoo.finance.configuration.ConfigManager;
import com.yahoo.finance.route.AbstractHttpHandler;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.impl.RouterImpl;

/**
 * 
 * @author tausif
 *
 */
public class MainVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
	private static Vertx vertx = Vertx.vertx();

	public static Vertx getVertx() {
		return vertx;
	}
	
	public static void main(String... args) {

		LOGGER.info("Starting Server...");
		
		try {
			LOGGER.info("Reading configuration file from directory " + ConfigManager.INSTANCE.getBaseDir());

			Router mainRouter = Router.router(vertx);
			
			HttpServerOptions options = new HttpServerOptions()
					.setMaxWebsocketFrameSize(Integer.parseInt(
							ConfigManager.INSTANCE.getAppConfig().getProperties().getProperty("app.server.framesize", "1000000")));
			
			loadServices(vertx, mainRouter);
			
			HttpServer server = vertx.createHttpServer(options);
			int port = Integer.parseInt(ConfigManager.INSTANCE.getAppConfig().getProperties().getProperty("app.port", "8080"));
			
			server.requestHandler(mainRouter::accept).listen(port);
			
			LOGGER.info("Application started at port " + port);
			
			
		} catch(Exception e) {
			LOGGER.error("Cannot start application ", e);
		}
	}

	private static void loadServices(Vertx vertx, Router mainRouter)
			throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		
		Properties services = ConfigManager.INSTANCE.getAppConfig().getPropertiesWithPrefix("service.api.loadclass");
		
		for(Object key : services.keySet()) {
			
			String className = services.getProperty(key.toString());
			Object newInstance = Class.forName(className).getConstructor().newInstance();
			
			if (newInstance instanceof AbstractHttpHandler) {
				RouterImpl routerImpl = new RouterImpl(vertx);

				Route route = routerImpl.route(newInstance.getClass().getAnnotation(UrlPath.class).method(),
						newInstance.getClass().getAnnotation(UrlPath.class).name());

				if(!newInstance.getClass().getAnnotation(UrlPath.class).consumes().isEmpty())
					route.consumes(newInstance.getClass().getAnnotation(UrlPath.class).consumes());
				if(!newInstance.getClass().getAnnotation(UrlPath.class).produces().isEmpty())
					route.produces(newInstance.getClass().getAnnotation(UrlPath.class).produces());

				route.handler((AbstractHttpHandler) newInstance);
				mainRouter.mountSubRouter("/", routerImpl);
			}
			else {
				LOGGER.warn("Cannot load API route service " + services.getProperty(key.toString()));
			}
		}
	}
}
