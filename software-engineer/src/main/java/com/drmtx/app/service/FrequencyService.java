package com.drmtx.app.service;

import com.drmtx.app.domain.Frequency;

import java.io.Serializable;
import java.util.List;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
public interface FrequencyService<E extends Frequency, ID extends Serializable> extends AbstractService<E, ID> {

	List<Frequency> findByAnalysisId(String analysisId, Integer count);

}
