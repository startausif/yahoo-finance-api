package com.yahoo.finance.exceptions;

import com.yahoo.finance.common.enums.Result;

/**
 * 
 * @author tausif
 *
 */
public class ApplicationException extends AbstractException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ApplicationException(String message) {
		super(message);
	}

	@Override
	public String getErrorMessage() {
		return Result.INTERNAL_ERROR.getResultMessage();
	}

	@Override
	public int getErrorCode() {
		return Result.INTERNAL_ERROR.getResultCode();
	}

}
