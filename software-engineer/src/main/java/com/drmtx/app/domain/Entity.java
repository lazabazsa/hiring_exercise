package com.drmtx.app.domain;

import org.springframework.data.domain.Persistable;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
public interface Entity extends Persistable<String> {
	void setId(String value);
}
