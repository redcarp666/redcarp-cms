package org.redcarp.cms.exception;

public class CategoryUpdateException extends RuntimeException {

	public CategoryUpdateException(String message) {
		super(message);
	}

	public CategoryUpdateException(String message, Throwable cause) {
		super(message, cause);
	}
}
