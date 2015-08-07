package com.drmtx.app.domain;

import java.util.Set;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
public interface Analysis extends Entity {
	String getRedditURL();

	void setRedditURL(String url);

	Long getNumberOfComments();

	void setNumberOfComments(Long numberOfComments);

	Set<Frequency> getFrequencies();

	void setFrequencies(Set<Frequency> frequencies);

	void addFrequency(Frequency frequency);

	void removeFrequency(Frequency frequency);
}
