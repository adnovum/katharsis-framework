package io.katharsis.jpa.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.katharsis.jpa.annotations.JpaResource;

@Entity
@JpaResource(type = "renamedResource")
public class RenamedTestEntity {

	@Id
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
