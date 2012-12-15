/**
 *
 */
package com.sourcecoding.multitenancy.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import com.sourcecoding.multitenancy.ManagedObject;
import com.sourcecoding.multitenancy.MultiTenancyEntity;
import com.sourcecoding.multitenancy.MultiTenancyPrincipal;
import com.sourcecoding.multitenancy.MultiTenancyService;

//@FilterDefs({ @FilterDef(name = "tenantFilter", parameters = { @ParamDef(name = "tenantFilterString", type = "string") }) })
//@Filters({ @Filter(name = "tenantFilter", condition = "(tenantId = :tenantFilterString))") })
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

	// @PrePersist
	public void onPersist() {
		if (tenantId == null) {
			MultiTenancyService mtService = ManagedObject
					.ejbLookup(MultiTenancyService.class);
			MultiTenancyPrincipal mtp = mtService.getPrincipal();
			tenantId = mtp.getTenant();
		}
	}

	// @PostLoad
	// @PreUpdate
	// @PreRemove
	public void onEntityModification() throws Exception {
		MultiTenancyService mtService = ManagedObject
				.ejbLookup(MultiTenancyService.class);
		MultiTenancyPrincipal mtp = mtService.getPrincipal();
		String currentTenant = mtp.getTenant();
		if (currentTenant == null)
			throw new RuntimeException("no tenant available!");
		if (!currentTenant.equals(tenantId))
			throw new RuntimeException("tenant do not match!");

	}
}
