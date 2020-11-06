package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Map<String, String> errors;
	
	public ValidationException(String msg) {
		super(msg);
		errors = new HashMap<>();
	}
	
	public Map<String, String> getErrors() {
		return errors;
	}
	public void addError(String fieldName, String errorMessage) {
		errors.put(fieldName, errorMessage);
	}
	
}
