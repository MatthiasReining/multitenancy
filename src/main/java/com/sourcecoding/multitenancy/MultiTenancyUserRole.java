package com.sourcecoding.multitenancy;

public interface MultiTenancyUserRole {

	public abstract <T extends MultiTenancyUser> T getMultiTenancyUser();

	public abstract <T extends MultiTenancyUser> void setMultiTenancyUser(
			T multiTenancyUser);

	public abstract String getRole();

	public abstract void setRole(String role);

}