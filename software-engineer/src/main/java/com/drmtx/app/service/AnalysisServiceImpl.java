package com.drmtx.app.service;

import com.drmtx.app.domain.Analysis;
import com.drmtx.app.repository.AnalysisRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
@Service
@Transactional(readOnly = true)
public class AnalysisServiceImpl extends AbstractServiceImpl<Analysis, String> implements AnalysisService<Analysis, String> {

	private static final Logger _logger = LogManager.getLogger(AnalysisServiceImpl.class);

	private AnalysisRepository analysisRepository;

	@Autowired
	public AnalysisServiceImpl(AnalysisRepository analysisRepository) {
		this.analysisRepository = analysisRepository;
	}

	@Override
	public JpaRepository<Analysis, String> getRepository() {
		return (JpaRepository) analysisRepository;
	}
}
