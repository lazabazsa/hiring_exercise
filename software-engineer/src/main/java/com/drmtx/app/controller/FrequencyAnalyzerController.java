package com.drmtx.app.controller;

import com.drmtx.app.domain.Analysis;
import com.drmtx.app.domain.AnalysisImpl;
import com.drmtx.app.domain.Frequency;
import com.drmtx.app.domain.FrequencyImpl;
import com.drmtx.app.exception.FrequencyAnalysisException;
import com.drmtx.app.service.AnalysisService;
import com.drmtx.app.service.FrequencyService;
import com.drmtx.app.service.web.RedditService;
import com.drmtx.app.util.AnalyzerUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
@Controller
@RequestMapping(value = "/frequency")
public class FrequencyAnalyzerController {

	private static final Logger _logger = LogManager.getLogger(FrequencyAnalyzerController.class);

	private static final String HOST_REDDIT = "reddit.com";
	private static final String HTTP_PROTOCOL = "http";
	private static final String COMMENTS = "comments";
	private static final String SLASH = "/";
	private static final String DOT_JSON = ".json";

	// Todo: Refactor the messages to ReloadableResourceBundleMessageSource
	private static final String PROVIDED_URL = "Provided \"url\" '%s' ";
	private static final String PROVIDED_REDDIT_URL = "Provided Reddit \"url\" '%s' ";
	private static final String EXAMPLE_REDDIT_URL = "\n\nExample:\n/frequency/new?url=https://www.reddit.com/r/java/" +
			"comments/32pj67/java_reference_in_gta_v_beautiful/.json";
	private static final String NOT_PROVIDED_URL = "'url' parameter must be provided." + EXAMPLE_REDDIT_URL;

	private static final String NOT_VALID_URL = PROVIDED_URL + "is not a valid URL!";
	private static final String NOT_VALID_REDDIT_URL = PROVIDED_URL + "is not a valid Reddit site!";
	private static final String URL_ERROR_REASON = "\nReason: ";

	private static final String REDDIT_URL_DOES_NOT_HAVE_COMMENTS_PART = PROVIDED_REDDIT_URL + "does not have " +
			"\"comments\" part!";
	private static final String REDDIT_URL_DOES_NOT_HAVE_ARTICLE_PART = PROVIDED_REDDIT_URL + "does not have article id!";
	private static final String REDDIT_URL_DOES_NOT_HAVE_DOT_JSON_PART = PROVIDED_REDDIT_URL + "does not have '" + DOT_JSON + "' at the end.";
	private static final String REDDIT_URL_HAS_INVALID_ARTICLE_PART = PROVIDED_REDDIT_URL + "has invalid article id!";
	private static final String ANALYSIS_CANNOT_BE_FOUND = "Analysis with the given id '%s' cannot be found.";
	private static final String COUNT_CANNOT_BE_LESS_THAN_ONE = "Count cannot be less then 1.";

	@Autowired
	private AnalysisService<Analysis, String> analysisService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private FrequencyService<Frequency, String> frequencyService;

	@Autowired
	private RedditService redditService;

	/**
	 * <p>This API endpoint which takes a Reddit comment URL as a parameter e.g.:
	 * https://www.reddit.com/r/java/comments/32pj67/java_reference_in_gta_v_beautiful/.json or https://www.reddit.com/comments/32pj67/.json
	 * <p>When the API is being called, the code does the following steps:
	 * <ul>
	 * <li>1. Validates the passed Reddit URL.</li>
	 * <li>2. Checks if the valid Reddit URL already contains the "comments", "articleId" and ".json" ending.</li>
	 * <li>3. Calls the Reddit public API to retrieve the json of the comments and parses/maps the JSON response into object model.</li>
	 * <li>4. Analyses the word frequency of all comments within the thread with following behavior:
	 * <ul>
	 * <li>All whitespace characters should be ignored</li>
	 * <li>All words should be converted to lower case</li>
	 * <li>Following punctuation characters should be ignored: !,.?</li>
	 * </ul>
	 * </li>
	 * <li>5. Persists the result.</li>
	 * <li>6. Returns the unique identifier.</li>
	 * </ul>
	 * </p>
	 * <p>Note: by adding .json to any Reddit comment URL the Reddit API returns a json representation of this particular comment
	 * thread, the json representation will be used for the word frequency analysis.</p>
	 *
	 * @param urlParam The reddit url
	 * @return a unique identifier of the analysis
	 * @throws FrequencyAnalysisException in case of any validation fails
	 */
	@ResponseBody
	@RequestMapping(value = "/new", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String newAnalysis(@RequestParam(value = "url") String urlParam) throws FrequencyAnalysisException {

		_logger.info("New Analysis Request start.");

		// Make urlParam lowercase
		urlParam = urlParam.toLowerCase();

		// Validate urlParam
		validateURL(urlParam);
		_logger.info("URL is fully valid.");

		// Make a HTTP request using the urlParam to retrieve the json of the comments.
		List<RedditService.RedditCommentJSONObject> jsonObjects = redditService.retrieveComments(urlParam);
		_logger.info("RedditCommentJSONObject(s) are retrieved.");


		// Analyze the response of the Reddit URL.
		AnalyzerUtil.WordAnalysis wordAnalysis = AnalyzerUtil.analyze(jsonObjects);
		_logger.info("Analysis is ready.");

		// wordAnalysis must not be null.
		Assert.notNull(wordAnalysis);

		// Persist the analysis and the results if there are any
		Map<String, Long> wordFrequency = wordAnalysis.getWordFrequency();
		_logger.info("Save an Analysis entity.");
		Analysis analysis = new AnalysisImpl();
		analysis.setNumberOfComments((long) wordAnalysis.getComments().size());
		analysis.setRedditURL(urlParam);
		analysisService.save(analysis);
		_logger.info("Analysis entity is saved.");

		if (!wordFrequency.isEmpty()) {
			_logger.info("Save all the Frequency entities.");
			for (String word : wordFrequency.keySet()) {
				Frequency frequency = new FrequencyImpl();
				analysis.addFrequency(frequency);
				frequency.setWord(word);
				frequency.setCount(wordFrequency.get(word));
				frequencyService.save(frequency);
			}
			_logger.info("All the Frequency entities are saved.");
		}

		// analysis' id must not be null.
		Assert.notNull(analysis.getId());
		// return the UID of the analysis
		_logger.info("Returning the Analysis UID.");
		return analysis.getId();
	}


	/**
	 * <p>This API endpoint which takes an Analysis UID as a path variable and a count (optional) parameter
	 * <ul>
	 * <li>1. Validates the passed Analysis UID.</li>
	 * <li>2. Validates the passed count value.</li>
	 * <li>3. Loads all the frequency entities for the given Analysis UID.</li>
	 * <li>4. Returns the result wrapped in a transfer object</li>
	 *
	 * @param id    the Analysis UID
	 * @param count (optional) is the limit for the result set
	 * @return the list of FrequencyJSONObject
	 * @throws FrequencyAnalysisException in case of any validation fails
	 */
	@ResponseBody
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FrequencyJSONObject> retrieveAnalysis(@PathVariable(value = "id") String id, @RequestParam(value = "count", required = false) Integer count) throws FrequencyAnalysisException {
		// path id must not be null.
		Assert.notNull(id);

		// load the analysis. if there is none for the given ID throw exception
		Analysis analysis = analysisService.findOne(id);
		if (analysis == null) {
			throw new FrequencyAnalysisException(HttpStatus.NOT_FOUND, String.format(ANALYSIS_CANNOT_BE_FOUND, id));
		}
		_logger.info("Analysis UID is valid.");

		// Validate count
		if (count != null && (count < 1 || count > Integer.MAX_VALUE)) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, COUNT_CANNOT_BE_LESS_THAN_ONE);
		}
		_logger.info("Count is either null or valid.");

		List<FrequencyJSONObject> result = new ArrayList<>();
		// Create the result array of FrequencyJSONObject if there are any frequencies
		List<Frequency> frequencies = frequencyService.findByAnalysisId(id, count);
		_logger.info("Frequency entities are loaded.");
		if (!CollectionUtils.isEmpty(frequencies)) {
			frequencies.forEach(frequency -> {
				result.add(new FrequencyJSONObject(frequency.getWord(), frequency.getCount()));
			});
		}

		_logger.info("Result JSON Object is ready, returning.");
		return result;
	}


	/**
	 * Validates whether the url is a valid Reddit Comment URL or not.
	 *
	 * @param urlParam a url which should be a valid Reddit Comment URL
	 * @throws FrequencyAnalysisException if the URL is not valid
	 */
	private void validateURL(String urlParam) throws FrequencyAnalysisException {
		_logger.info("URL validation starts.");
		// The URL parameter must be always provided, therefore we check it.
		if (!StringUtils.hasText(urlParam)) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, NOT_PROVIDED_URL);
		}

		// Create a URL object to be able to validate the passed urlParam
		URL url;
		try {
			url = new URL(urlParam);
		} catch (MalformedURLException e) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, String.format(NOT_VALID_URL, urlParam) + URL_ERROR_REASON + e.getMessage());
		}
		_logger.info("URL is a valid URL.");

		// URL object must not be null.
		Assert.notNull(url);

		// Check if the URL object has either HTTP or HTTPS protocol and the host name is reddit.
		if (!url.getProtocol().contains(HTTP_PROTOCOL) || !url.getHost().contains(HOST_REDDIT)) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, String.format(NOT_VALID_REDDIT_URL, urlParam));
		}
		_logger.info("URL is a valid Reddit URL.");


		// Check if the urlPath contains the comments and the article id.
		// Comments part
		String urlPath = url.getPath();
		String[] splitUrlPath = urlPath.split(SLASH);
		List<String> urlPathElements = Arrays.asList(splitUrlPath);
		if (CollectionUtils.isEmpty(urlPathElements)) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, String.format(REDDIT_URL_DOES_NOT_HAVE_COMMENTS_PART, urlParam));
		}
		boolean containsCommentsPath = false;
		for (String urlPathElement : urlPathElements) {
			containsCommentsPath = urlPathElement.contains(COMMENTS);
			if (containsCommentsPath) {
				break;
			}
		}
		if (!containsCommentsPath) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, String.format(REDDIT_URL_DOES_NOT_HAVE_COMMENTS_PART, urlParam));
		}
		_logger.info("URL has a valid 'comments' part.");

		// .JSON part
		if (!urlPath.endsWith(DOT_JSON)) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, String.format(REDDIT_URL_DOES_NOT_HAVE_DOT_JSON_PART, urlParam));
		}
		_logger.info("URL has a valid '.json' part.");

		// Article ID36 part
		int articlePathIndex = urlPathElements.indexOf(COMMENTS) + 1;
		if (articlePathIndex == 0) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, String.format(REDDIT_URL_DOES_NOT_HAVE_ARTICLE_PART, urlParam));
		}
		String articleID36 = urlPathElements.get(articlePathIndex);
		if (!StringUtils.hasText(articleID36)) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, String.format(REDDIT_URL_DOES_NOT_HAVE_ARTICLE_PART, urlParam));
		}
		Pattern pattern = Pattern.compile("[a-z0-9]+");
		Matcher matcher = pattern.matcher(articleID36);
		boolean matches = matcher.matches();
		if (!matches) {
			throw new FrequencyAnalysisException(HttpStatus.BAD_REQUEST, String.format(REDDIT_URL_HAS_INVALID_ARTICLE_PART, urlParam));
		}
		_logger.info("URL has a valid 'articleID36' part.");
	}

	/**
	 * The Response object which is used by the <code>retrieveAnalysis()</code> request mapper method.
	 */
	private static class FrequencyJSONObject {

		private String word;
		private Long count;

		public FrequencyJSONObject(String word, Long count) {
			this.word = word;
			this.count = count;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}
	}
}
