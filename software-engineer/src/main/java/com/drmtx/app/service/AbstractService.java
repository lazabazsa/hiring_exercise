package com.drmtx.app.service;

import com.drmtx.app.domain.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
public interface AbstractService<E extends Entity, ID extends Serializable> {

	E save(E entity);

	<F extends ID> E findOne(F id);

	List<E> findAll();

	void delete(E entity);

	void deleteAllInBatch();

	long count();
}
