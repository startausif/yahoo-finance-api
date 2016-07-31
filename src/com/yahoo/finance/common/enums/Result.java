package com.yahoo.finance.common.enums;


/**
 * @author tausif
 *
 */
public enum Result {
	BAD_REQUEST(3, "BAD_REQUEST"),
	
	
	INTERNAL_ERROR(599, "INTERNAL_ERROR"), 
	UNKNOWN_ERROR(600, "UNKNOWN_ERROR"), 
	INVALID_URL(601, "INVALID_URL");
	
	private int resultCode;
	private String resultMessage;
	
	private Result(int resultCode, String resultMessage) {
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}

	public int getResultCode() {
		return resultCode;
	}
	
	public String getResultMessage() {
		return resultMessage;
	}
}
