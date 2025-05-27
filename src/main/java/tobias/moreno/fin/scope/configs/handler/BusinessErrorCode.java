package tobias.moreno.fin.scope.configs.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Getter
public enum BusinessErrorCode {

	NO_CODE(0, "No code", NOT_IMPLEMENTED),
	INCORRECT_CURRENT_PASSWORD(300, "Incorrect current password", BAD_REQUEST),
	NEW_PASSWORD_DOES_NOT_MATCH(301, "The new password doesn´t match", BAD_REQUEST),
	ACCOUNT_LOCKED(302, "User account is locked", FORBIDDEN),
	ACCOUNT_DISABLED(303, "User account is locked", FORBIDDEN),
	BAD_CREDENTIALS(304, "Login and/or password is incorrect", FORBIDDEN),;


	private final int code;
	private final String description;
	private final HttpStatus httpStatus;

	BusinessErrorCode(int code, String description, HttpStatus httpStatus) {
		this.code = code;
		this.description = description;
		this.httpStatus = httpStatus;

	}
}
