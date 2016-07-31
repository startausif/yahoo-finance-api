package com.yahoo.finance.exceptions;

import com.yahoo.finance.common.enums.Result;

/**
 * @author tausif
 *
 */
public class ValidationException extends AbstractException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public ValidationException(String message) {
		super(message);
	}
	
	@Override
	public String getErrorMessage() {
		return Result.BAD_REQUEST.getResultMessage();
	}

	@Override
	public int getErrorCode() {
		return Result.BAD_REQUEST.getResultCode();
	}
}
