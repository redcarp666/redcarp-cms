package org.redcarp.cms.exception;

public class UserRolePermissionException extends RuntimeException {

	public UserRolePermissionException(String message) {
		super(message);
	}

	public UserRolePermissionException(String message, Throwable cause) {
		super(message, cause);
	}
}
