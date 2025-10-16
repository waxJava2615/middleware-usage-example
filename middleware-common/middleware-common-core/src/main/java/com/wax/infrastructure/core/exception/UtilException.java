package com.wax.infrastructure.core.exception;

public class UtilException extends RuntimeException {

	public UtilException(Throwable e) {
		super(e.getMessage(), e);
	}

	public UtilException(String message) {
		super(message);
	}


	public UtilException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public UtilException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

}