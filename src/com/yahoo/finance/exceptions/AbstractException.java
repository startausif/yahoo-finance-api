
package com.yahoo.finance.exceptions;

/**
 * @author tausif
 *
 */
public abstract class AbstractException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AbstractException() {
		this("Application Exception occured");
	}
	
	public AbstractException(String message) {
		super(message);
	}

	public abstract String getErrorMessage();

	public abstract int getErrorCode();

}
