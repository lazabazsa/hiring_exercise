package com.drmtx.app.controller;

import com.drmtx.app.exception.FrequencyAnalysisException;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 * <p>
 * This Controller is responsible to send JSON response to the client with all the information regarding
 * any Exception.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
@RestController
@RequestMapping(value = "/error")
public class FrequencyAnalyzerErrorController implements ErrorController {

	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> error(HttpServletRequest request) {
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		return errorAttributes().getErrorAttributes(requestAttributes, false);
	}

	@Bean
	protected ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {

			@Override
			public Map<String, Object> getErrorAttributes(
					RequestAttributes requestAttributes,
					boolean includeStackTrace) {
				Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
				Throwable error = super.getError(requestAttributes);
				if (error instanceof FrequencyAnalysisException) {
					FrequencyAnalysisException frequencyAnalysisException = (FrequencyAnalysisException) error;
					errorAttributes.put("status", frequencyAnalysisException.getStatusCode().value());
					errorAttributes.put("error", frequencyAnalysisException.getStatusCode().getReasonPhrase());
				}
				return errorAttributes;
			}

		};
	}
}
