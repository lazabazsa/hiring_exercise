package com.drmtx.app.service.web;

import com.drmtx.app.exception.FrequencyAnalysisException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
public interface RedditService {

	List<RedditCommentJSONObject> retrieveComments(String url) throws FrequencyAnalysisException;

	/**
	 * This class is used internally
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	class RedditCommentJSONObject {

		private String name;
		private String kind;
		private String modhash;
		private RedditCommentJSONObject data;
		private List<RedditCommentJSONObject> children;
		private RedditCommentJSONObject replies;
		private String body;

		public RedditCommentJSONObject() {

		}

		public RedditCommentJSONObject(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getKind() {
			return kind;
		}

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getModhash() {
			return modhash;
		}

		public void setModhash(String modhash) {
			this.modhash = modhash;
		}

		public RedditCommentJSONObject getData() {
			return data;
		}

		public void setData(RedditCommentJSONObject data) {
			this.data = data;
		}

		public List<RedditCommentJSONObject> getChildren() {
			return children;
		}

		public void setChildren(List<RedditCommentJSONObject> children) {
			this.children = children;
		}

		public RedditCommentJSONObject getReplies() {
			return replies;
		}

		public void setReplies(RedditCommentJSONObject replies) {
			this.replies = replies;
		}
	}
}
