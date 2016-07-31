package com.yahoo.finance.route;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yahoo.finance.common.annotation.UrlPath;
import com.yahoo.finance.common.enums.ErrorConstant;
import com.yahoo.finance.exceptions.AbstractException;
import com.yahoo.finance.exceptions.ValidationException;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

/**
 * Generic class to handle JSON kind of route.
 * 
 * 
 * @author tausif
 *
 * @param <R>
 * @param <T>
 */
public abstract class AbstractJsonHandler<R, T> extends AbstractHttpHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJsonHandler.class);

	@Override
	protected Object readBufferedRequest(Buffer buffer) throws AbstractException {

		JsonObject jsonObject = new JsonObject();
		Object readValue = null;
		ObjectMapper mapper = new ObjectMapper();
		try {

			Pattern pattern = Pattern.compile("([A-Za-z]|\\.)+");
			Matcher matcher = pattern.matcher(getClass().getGenericSuperclass().getTypeName());

			matcher.find();
			matcher.find(); // We want to read the request class specified by <R, T>
			if(buffer.getBytes().length > 0)
			readValue = mapper.readValue(buffer.getBytes(), Class.forName(matcher.group()));

		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Error parsing JSON ", e);
			jsonObject.put("errorCode", ErrorConstant.INVALID_REQUEST.getCode());
			jsonObject.put("errorMessage", ErrorConstant.INVALID_REQUEST.getMessage());
			jsonObject.put("errorDescription", e.getMessage());
			throw new ValidationException(jsonObject.toString());
		} catch (IOException e) {
			LOGGER.error("Error reading stream", e);
			jsonObject.put("errorCode", ErrorConstant.INVALID_REQUEST.getCode());
			jsonObject.put("errorMessage", ErrorConstant.INVALID_REQUEST.getMessage());
			jsonObject.put("errorDescription", e.getMessage());
			throw new ValidationException(jsonObject.toString());
		} catch (ClassNotFoundException e) {
			LOGGER.error("Error reading buffer", e);
			throw new ValidationException(jsonObject.toString());
		}

		return readValue;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void handleRequest(Object r) throws AbstractException {
		R r1 = (R) r;
		performTask(r1);
	}

	@Override
	@SuppressWarnings({ "null" })
	protected JsonObject formatResponse(Object r) throws AbstractException {
		ObjectMapper mapper = new ObjectMapper();
		JsonObject jsonObject = null;

		try {
			String jsonAsString = mapper.writeValueAsString(r);
			jsonObject = new JsonObject(jsonAsString);

		} catch (AbstractException e) {
			LOGGER.error("Error performing task");
			jsonObject.put("errorCode", ErrorConstant.INVALID_REQUEST.getCode());
			jsonObject.put("errorMessage", ErrorConstant.INVALID_REQUEST.getMessage());
		} catch (JsonProcessingException e) {
			LOGGER.error("Error creating JSON", e);
			jsonObject.put("message", e.getMessage());
		}
		return jsonObject;
	}

	protected abstract void performTask(R request) throws AbstractException;

	@Override
	protected Map<String, String> getHeaders() {
		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("content-type", getClass().getAnnotation(UrlPath.class).produces());
		return hashMap;
	}

}
