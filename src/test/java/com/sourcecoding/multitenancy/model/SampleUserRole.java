package com.sourcecoding.multitenancy.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.sourcecoding.multitenancy.AbstractMultiTenancyUserRole;
import com.sourcecoding.multitenancy.MultiTenancyUser;

@Entity
public class SampleUserRole extends AbstractMultiTenancyUserRole {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private SampleUser multiTenancyUser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MultiTenancyUser> T getMultiTenancyUser() {
		return (T) multiTenancyUser;
	}

	@Override
	public <T extends MultiTenancyUser> void setMultiTenancyUser(
			T multiTenancyUser) {
		this.multiTenancyUser = (SampleUser) multiTenancyUser;

	}

}
