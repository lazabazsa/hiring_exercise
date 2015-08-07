package com.drmtx.app.service.web;

import com.drmtx.app.exception.FrequencyAnalysisException;
import com.drmtx.app.exception.RedditServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
@Service
public class RedditServiceImpl implements RedditService {

	private static final Logger _logger = LogManager.getLogger(RedditServiceImpl.class);

	@Override
	public List<RedditCommentJSONObject> retrieveComments(String url) throws FrequencyAnalysisException {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> jsonResponse = null;
		try {
			jsonResponse = restTemplate.getForEntity(url, String.class);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
			mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
			List<RedditCommentJSONObject> jsonObjects = mapper.readValue(jsonResponse.getBody(), new TypeReference<List<RedditCommentJSONObject>>() {
			});
			_logger.debug(jsonObjects);
			return jsonObjects;
		} catch (HttpStatusCodeException e) {
			throw new RedditServiceException(e.getStatusCode(), e.getMessage());
		} catch (IOException e) {
			throw new FrequencyAnalysisException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
