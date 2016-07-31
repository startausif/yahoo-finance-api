package com.yahoo.finance.common.enums;

public enum ErrorConstant {

	INVALID_REQUEST(151, "Request is invalid"),
	INTERNAL_ERROR(503, "Internal Error"),
	;
	
	private ErrorConstant(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	
	private int code;
	private String message;
	
}
