package com.drmtx.test;

import com.drmtx.app.Application;
import com.drmtx.app.domain.Analysis;
import com.drmtx.app.domain.AnalysisImpl;
import com.drmtx.app.domain.Frequency;
import com.drmtx.app.domain.FrequencyImpl;
import com.drmtx.app.exception.RedditServiceException;
import com.drmtx.app.service.AnalysisService;
import com.drmtx.app.service.FrequencyService;
import com.drmtx.app.service.web.RedditService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.List;
import java.util.Random;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTest {

	private static final Logger _logger = LogManager.getLogger(ApplicationTest.class);

	public static final String VALID_REDDIT_JSON_URL = "https://www.reddit.com/comments/122def/.json";
	private MockMvc mockMvc;
	private RestTemplate restTemplate;

	@Autowired
	private RedditService redditService;

	@Autowired
	private AnalysisService<Analysis, String> analysisService;


	@Autowired
	private FrequencyService<Frequency, String> frequencyService;

	@Autowired
	private WebApplicationContext webApplicationContext;


	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
		restTemplate = new TestRestTemplate();
	}

	/* Testing /frequency/new */
	@Test()
	public void createAnalysisWithoutURLParam() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/frequency/new")
						.accept(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test(expected = NestedServletException.class)
	public void createAnalysisWithURLParamWithEmptyValue() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/frequency/new")
						.param("url", "")
						.accept(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test(expected = NestedServletException.class)
	public void createAnalysisWithURLParamWithInvalidValue() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/frequency/new")
						.param("url", "12345")
						.accept(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test(expected = NestedServletException.class)
	public void createAnalysisWithURLParamWithInvalidProtocol() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/frequency/new")
						.param("url", "file://www.reddit.com")
						.accept(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test(expected = NestedServletException.class)
	public void createAnalysisWithURLParamWithInvalidCommentPath() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/frequency/new")
						.param("url", "https://www.reddit.com/comment/122def")
						.accept(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test(expected = NestedServletException.class)
	public void createAnalysisWithURLParamWithInvalidDotJSONEnding() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/frequency/new")
						.param("url", "https://www.reddit.com/comments/122def/")
						.accept(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/frequency/new")
						.param("url", "https://www.reddit.com/r/java/comments/32pj67/java_reference_in_gta_v_beautiful/")
						.accept(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test()
	public void createAnalysisWithValidURLParam() {
		try {
			this.mockMvc
					.perform(MockMvcRequestBuilders.get("/frequency/new")
							.param("url", "https://www.reddit.com/comments/122def/.json")
							.accept(MediaType.TEXT_PLAIN))
					.andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

		} catch (Exception ex) {
			Assert.assertThat(ex, CoreMatchers.instanceOf(NestedServletException.class));
		}

	}

	/* Test Reddit Service */
	@Test()
	public void testRedditServiceResultNotNull() {
		try {
			List<RedditService.RedditCommentJSONObject> redditCommentJSONObjects =
					this.redditService.retrieveComments(VALID_REDDIT_JSON_URL);

			Assert.assertThat(redditCommentJSONObjects, CoreMatchers.notNullValue());

		} catch (Exception ex) {
			Assert.assertThat(ex, CoreMatchers.instanceOf(RedditServiceException.class));
		}

	}

	/* Test /frequency/{uid} */
	@Test()
	public void retrieveAnalysisWithInvalidUID() throws Exception {
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/frequency")
						.requestAttr("id", "ABC")
						.accept(MediaType.TEXT_PLAIN))
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	/* Test Analysis and Frequency Service */
	@Test()
	public void persistAnalysisAndFrequencies() throws Exception {
		Analysis testAnalysis = new AnalysisImpl();
		testAnalysis.setRedditURL("https://www.reddit.com/r/java/comments/32pj67/java_reference_in_gta_v_beautiful/.json");
		testAnalysis.setNumberOfComments(200L);
		analysisService.save(testAnalysis);

		String testAnalysisId = testAnalysis.getId();
		Assert.assertNotNull(testAnalysisId);
		for (int count = 0; count < 10; count++) {
			Frequency frequency = new FrequencyImpl();
			testAnalysis.addFrequency(frequency);

			// Generate words
			Random random = new Random();
			// words of length 3 through 10.
			char[] word = new char[random.nextInt(8) + 3];
			for (int j = 0; j < word.length; j++) {
				word[j] = (char) ('a' + random.nextInt(26));
			}
			frequency.setWord(String.valueOf(word));
			frequency.setCount((long) random.nextInt(20));
			frequencyService.save(frequency);
		}

		// Test using no count
		List<Frequency> frequencies = frequencyService.findByAnalysisId(testAnalysisId, null);
		Assert.assertNotNull(frequencies);
		Assert.assertEquals(frequencies.size(), 10);

		// Test using count
		List<Frequency> limitedFrequencies = frequencyService.findByAnalysisId(testAnalysisId, 4);

		Assert.assertEquals(limitedFrequencies.size(), 4);
	}
}
