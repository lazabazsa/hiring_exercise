package com.drmtx.app.domain;

import org.springframework.util.Assert;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * Frequency entity which holds info about a word and its frequency within an Analyzed Reddit URL.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
@Entity
@Table(name = "frequency", indexes = {@Index(columnList = "count")})
public class FrequencyImpl extends EntityImpl implements Frequency {

	@ManyToOne(targetEntity = AnalysisImpl.class)
	@JoinColumn(name = "analysis_id", nullable = false, updatable = false)
	private Analysis analysis;

	@Column(nullable = false)
	private String word;

	@Column(nullable = false)
	private Long count;

	public FrequencyImpl() {
	}

	@Override
	public Analysis getAnalysis() {
		return analysis;
	}

	@Override
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	@Override
	public String getWord() {
		return word;
	}

	@Override
	public void setWord(String word) {
		this.word = word;
	}

	@Override
	public Long getCount() {
		return count;
	}

	@Override
	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public int compareTo(Frequency other) {
		Assert.notNull(this.getCount());
		Assert.notNull(other.getCount());
		return other.getCount().compareTo(this.getCount());
	}

	@Override
	public String toString() {
		return "FrequencyImpl{" +
				"analysis=" + analysis.getId() +
				", word='" + word + '\'' +
				", count=" + count +
				'}';
	}
}
