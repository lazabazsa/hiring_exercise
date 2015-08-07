package com.drmtx.app.domain;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
public interface Frequency extends Entity, Comparable<Frequency> {

	Analysis getAnalysis();

	void setAnalysis(Analysis analysis);

	String getWord();

	void setWord(String word);

	Long getCount();

	void setCount(Long frequency);
}
