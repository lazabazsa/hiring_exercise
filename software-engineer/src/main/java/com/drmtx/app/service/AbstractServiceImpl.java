package com.drmtx.app.service;

import com.drmtx.app.domain.Entity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
@Service
@Transactional
public abstract class AbstractServiceImpl<E extends Entity, ID extends Serializable> implements AbstractService<E, ID> {

	private static final Logger _logger = LogManager.getLogger(AbstractServiceImpl.class);

	public abstract JpaRepository<E, ID> getRepository();

	@Override
	public E save(E entity) {
		_logger.debug("Saving entity: " + entity.getClass().getSimpleName());
		return getRepository().save(entity);
	}

	@Override
	public <F extends ID> E findOne(F id) {
		_logger.debug("Finding an entity for id: " + id);
		return getRepository().findOne(id);
	}

	@Override
	public List<E> findAll() {
		_logger.debug("Finding all the entities.");
		return getRepository().findAll();
	}

	@Override
	public void delete(E entity) {
		_logger.debug("Deleting entity: " + entity.getClass().getSimpleName());
		getRepository().delete(entity);
	}

	@Override
	public void deleteAllInBatch() {
		_logger.debug("Deleting entities in a Batch Call");
		getRepository().deleteAllInBatch();
	}

	@Override
	public long count() {
		_logger.debug("Retrieving count()");
		return getRepository().count();
	}
}
