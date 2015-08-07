package com.drmtx.app.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Digital River's Hiring Exercise for Software Engineer position.
 *
 * @author Balazs Torok
 * @since 04.08.2015
 */
@MappedSuperclass
public class EntityImpl implements Entity {

	// An Auto Generated id
	@GeneratedValue(generator = "uuid", strategy = GenerationType.AUTO)
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "id", unique = true, nullable = false)
	@Id
	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isNew() {
		return id == null;
	}

	@Override
	public void setId(String value) {
		this.id = value;
	}
}
