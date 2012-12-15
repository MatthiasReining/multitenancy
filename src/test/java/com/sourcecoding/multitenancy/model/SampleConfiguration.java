package com.sourcecoding.multitenancy.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "SAM_CONF")
@Entity
public class SampleConfiguration extends SampleMultiTenancyEntity {

	private static final long serialVersionUID = 1L;

	private String test;

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}
}
