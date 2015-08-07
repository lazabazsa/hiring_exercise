package com.drmtx.app.util;

import com.drmtx.app.service.web.RedditService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
public class AnalyzerUtil {

	public static synchronized WordAnalysis analyze(List<RedditService.RedditCommentJSONObject> jsonObjects) {
		WordAnalysis analysis = new WordAnalysis();

		for (RedditService.RedditCommentJSONObject jsonObject : jsonObjects) {

			collectComments(jsonObject, analysis);
			analyzeComments(analysis);
		}
		return analysis;
	}

	private static synchronized void collectComments(RedditService.RedditCommentJSONObject json, WordAnalysis analysis) {
		// Append the body to the result string builder
		if (StringUtils.hasText(json.getBody())) {
			analysis.addComment(json.getBody());
		}

		// WARNING: Recursive call: if the Data object is not null we process it
		if (json.getData() != null) {
			collectComments(json.getData(), analysis);
		}

		// WARNING: Recursive call: if the Replies object is not null we process it
		if (json.getReplies() != null) {
			collectComments(json.getReplies(), analysis);
		}

		// WARNING: Recursive call: if the json object contains Children we process each of them
		if (!CollectionUtils.isEmpty(json.getChildren())) {
			for (RedditService.RedditCommentJSONObject jsonObject : json.getChildren()) {
				collectComments(jsonObject, analysis);
			}
		}
	}

	private static void analyzeComments(WordAnalysis analysis) {
		Map<String, Long> result = new HashMap<>();

		analysis.getComments().stream().filter(StringUtils::hasText).forEach(comment -> {
//			String input = "The quick  !fuck!  \n brown fox \t jumps,     over     the lazy dog.\n";
//			String alpha = input.replaceAll("[!,\\.\\?]", "");
//			System.out.println(alpha);

			// Trim the comment, clean it from the punctuation characters and lowercase it
			comment = comment.replaceAll("[!,\\.\\?]", " ").trim().toLowerCase();

			Scanner scanner = new Scanner(comment);
			while (scanner.hasNext()) {
				String nextWord = scanner.next();
				if (result.containsKey(nextWord)) {
					Long aLong = result.get(nextWord);
					result.replace(nextWord, ++aLong);
				} else {
					result.put(nextWord, 1L);
				}
			}

		});
		analysis.setWordFrequency(result);
	}


	public static class WordAnalysis {

		private List<String> comments = new ArrayList<>();
		private Map<String, Long> wordFrequency = new HashMap<>();

		public List<String> getComments() {
			return comments;
		}

		public void addComment(String comment) {
			this.comments.add(comment);
		}

		public Map<String, Long> getWordFrequency() {
			return wordFrequency;
		}

		public void setWordFrequency(Map<String, Long> wordFrequency) {
			this.wordFrequency = wordFrequency;
		}
	}
}
