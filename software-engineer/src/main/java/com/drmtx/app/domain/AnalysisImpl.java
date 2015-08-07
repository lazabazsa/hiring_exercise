package com.drmtx.app.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * Analysis entity which holds the info about the Reddit URL, the number of comments and
 * a Set of Frequency entities.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
@Table(name = "analysis")
@javax.persistence.Entity
public class AnalysisImpl extends EntityImpl implements Analysis {

	@Column(name = "url_reddit", nullable = false, updatable = false)
	private String redditURL;

	@Column(name = "nmbr_of_cmmnts", nullable = false, updatable = false)
	private Long numberOfComments;

	@OneToMany(targetEntity = FrequencyImpl.class, cascade= CascadeType.ALL, mappedBy="analysis")
	private Set<Frequency> frequencies = new HashSet<>(0);

	public AnalysisImpl() {
	}

	@Override
	public String getRedditURL() {
		return redditURL;
	}

	@Override
	public void setRedditURL(String redditURL) {
		this.redditURL = redditURL;
	}

	@Override
	public Long getNumberOfComments() {
		return numberOfComments;
	}

	@Override
	public void setNumberOfComments(Long numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public Set<Frequency> getFrequencies() {
		return frequencies;
	}

	public void setFrequencies(Set<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	public void addFrequency(Frequency frequency) {
		frequency.setAnalysis(this);
		this.frequencies.add(frequency);
	}

	public void removeFrequency(Frequency frequency) {
		frequency.setAnalysis(null);
		this.frequencies.remove(frequency);
	}
}
