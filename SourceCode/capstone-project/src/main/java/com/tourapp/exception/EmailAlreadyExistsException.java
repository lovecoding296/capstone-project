package com.tourapp.exception;

public class EmailAlreadyExistsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmailAlreadyExistsException(String message) {
		super(message); // Truyền thông điệp vào lớp cha
		System.out.println("Error EmailAlreadyExistsException " + message);

	}

	public EmailAlreadyExistsException(String message, Throwable cause) {

		super(message, cause); // Truyền thông điệp và nguyên nhân vào lớp cha
		System.out.println("Error EmailAlreadyExistsException " + message);
	}
}
