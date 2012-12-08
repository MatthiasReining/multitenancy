/**
 *
 */
package com.sourcecoding.multitenancy.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity containing fields which are used by different entities.
 * 
 * @author spa
 * 
 */
@MappedSuperclass
public class AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created = new Date();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreated() {
		return (created == null) ? null : new Date(created.getTime());
	}

	public void setCreated(Date created) {
		this.created = (created == null) ? null : new Date(created.getTime());
	}
}
