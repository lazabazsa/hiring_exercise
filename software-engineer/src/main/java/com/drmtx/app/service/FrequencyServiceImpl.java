package com.drmtx.app.service;

import com.drmtx.app.domain.Frequency;
import com.drmtx.app.repository.FrequencyRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
@Service
@Transactional(readOnly = true)
public class FrequencyServiceImpl extends AbstractServiceImpl<Frequency, String> implements FrequencyService<Frequency, String> {

	private static final Logger _logger = LogManager.getLogger(FrequencyServiceImpl.class);

	private FrequencyRepository frequencyRepository;

	@Autowired
	public FrequencyServiceImpl(FrequencyRepository frequencyRepository) {
		this.frequencyRepository = frequencyRepository;
	}

	@Override
	public List<Frequency> findByAnalysisId(String analysisId, Integer count) {
		PageRequest pageRequest = new PageRequest(0, count != null ? count : Integer.MAX_VALUE);
		List<Frequency> frequencies = frequencyRepository.findByAnalysisId(analysisId, pageRequest);
		return frequencies;
	}

	@Override
	public JpaRepository<Frequency, String> getRepository() {
		return (JpaRepository) frequencyRepository;
	}
}
