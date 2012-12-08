package com.sourcecoding.multitenancy;

import java.util.List;

public interface MultiTenancyUser {

	public abstract String getUsername();

	public abstract void setUsername(String username);

	public abstract String getPassword();

	public abstract void setPassword(String password);

	public abstract String getTenant();

	public abstract void setTenant(String tenant);

	public abstract List<? extends MultiTenancyUserRole> getRoles();

	public abstract void setRoles(List<? extends MultiTenancyUserRole> roles);

}