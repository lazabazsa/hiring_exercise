package com.drmtx.app.repository;

import com.drmtx.app.domain.AnalysisImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 05.08.2015
 */
@Repository
@Transactional
public interface AnalysisRepository extends JpaRepository<AnalysisImpl, String> {
}
