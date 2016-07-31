package com.yahoo.finance.common.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.vertx.core.http.HttpMethod;

@Retention(RUNTIME)
@Target(TYPE)
public @interface UrlPath {

	String name() default "";
	
	String consumes() default "";
	
	String produces() default "";
	
	HttpMethod method() default HttpMethod.GET;
}
