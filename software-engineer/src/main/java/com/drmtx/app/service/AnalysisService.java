package com.drmtx.app.service;

import com.drmtx.app.domain.Analysis;

import java.io.Serializable;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
public interface AnalysisService<E extends Analysis, ID extends Serializable> extends AbstractService<E, ID> {
}
