package com.sourcecoding.multitenancy;

public class AuthenticationEvent {

	private MultiTenancyUser user;
	private boolean authenticated;

	public AuthenticationEvent(MultiTenancyUser user, boolean authenticated) {
		this.user = user;
		this.authenticated = authenticated;
	}

	public MultiTenancyUser getUser() {
		return user;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

}
