package com.drmtx.app.exception;

import org.springframework.http.HttpStatus;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 06.08.2015
 */
public class RedditServiceException extends FrequencyAnalysisException {

	public RedditServiceException(String message) {
		super(message);
	}

	public RedditServiceException(HttpStatus statusCode, String message) {
		super(statusCode, message);
	}
}
