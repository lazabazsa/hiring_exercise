package com.drmtx.app.exception;

import org.springframework.http.HttpStatus;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
public class FrequencyAnalysisException extends RuntimeException {
	private HttpStatus statusCode;

	public FrequencyAnalysisException(String message) {
		this(null, message);
	}

	public FrequencyAnalysisException(HttpStatus statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}
}
