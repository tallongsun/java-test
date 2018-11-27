package com.dl.resteasy.service;

public class EntityNotFoundException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7033548543241836196L;

	/**
	 * 
	 */
	public EntityNotFoundException() {
	}

	/**
	 * @param message
	 */
	public EntityNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
