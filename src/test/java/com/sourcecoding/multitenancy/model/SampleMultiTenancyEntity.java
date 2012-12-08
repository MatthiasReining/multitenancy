/**
 *
 */
package com.sourcecoding.multitenancy.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import com.sourcecoding.multitenancy.MultiTenancyEntity;

@MappedSuperclass
public class SampleMultiTenancyEntity extends AbstractEntity implements
		Serializable, MultiTenancyEntity {

	private static final long serialVersionUID = 1L;

	private String tenantId;

	@Override
	public String getTenantId() {
		return tenantId;
	}

	@Override
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

}
