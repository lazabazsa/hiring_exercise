package com.drmtx.app.repository;

import com.drmtx.app.domain.Frequency;
import com.drmtx.app.domain.FrequencyImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
@Repository
@Transactional
public interface FrequencyRepository extends JpaRepository<FrequencyImpl, String> {

	String FIND_BY_ANALYSIS_ORDER_BY_COUNT_DESC_LIMIT_QUERY = "SELECT f FROM FrequencyImpl f LEFT OUTER JOIN " +
			"f.analysis a WHERE a.id = :analysisId order by f.count desc";

	@Query(FIND_BY_ANALYSIS_ORDER_BY_COUNT_DESC_LIMIT_QUERY)
	List<Frequency> findByAnalysisId(@Param("analysisId") String analysisId, Pageable pageable);
}
