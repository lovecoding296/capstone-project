package funix.tca.exception;

public class EmailVerificationException extends Exception {
	private static final long serialVersionUID = 1L;

	public EmailVerificationException(String message) {
		super(message); // Truyền thông điệp vào lớp cha

	}

	public EmailVerificationException(String message, Throwable cause) {
		super(message, cause); // Truyền thông điệp và nguyên nhân vào lớp cha
	}
}