/*
 * Copyright (C) 2012 sourcecoding.com / Matthias Reining
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sourcecoding.multitenancy;

import java.security.Principal;
import java.security.acl.Group;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.jboss.security.ErrorCodes;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.DatabaseServerLoginModule;

/**
 * @author matthias reining
 */
public class MultiTenancyDatabaseServerLoginModule extends
		DatabaseServerLoginModule {

	public static final String CLIENT_DELIMITER = "#";

	@Override
	protected String getUsersPassword() throws LoginException {
		Principal p = getIdentity();
		if (!(p instanceof MultiTenancyPrincipal))
			return super.getUsersPassword();

		return ((MultiTenancyPrincipal) p).mtu.getPassword();
	}

	@Override
	protected boolean validatePassword(String inputPassword,
			String expectedPassword) {
		Principal p = getIdentity();
		if (!(p instanceof MultiTenancyPrincipal))
			return super.validatePassword(inputPassword, expectedPassword);

		boolean ok = SecurityTools.checkPassword(inputPassword,
				expectedPassword);

		MultiTenancyService mtService = ManagedObject
				.ejbLookup(MultiTenancyService.class);

		mtService.handleAuthentication((MultiTenancyPrincipal) getIdentity(),
				ok);

		return ok;
	}

	@Override
	protected Principal createIdentity(String username) throws Exception {

		// username format is defined as tenant + CLIENT_DELIMITER + username

		int i = username.indexOf(CLIENT_DELIMITER);
		if (i == -1)
			throw new LoginException(ErrorCodes.WRONG_FORMAT
					+ "A multi-tenancy 'username' must have a delimiter ('"
					+ CLIENT_DELIMITER + "')!");
		String tenant = username.substring(0, i);
		username = username.substring(i + 1);

		MultiTenancyService mtService = ManagedObject
				.ejbLookup(MultiTenancyService.class);
		try {
			MultiTenancyUser mtu = mtService.getMultiTenancyUser(tenant,
					username);
			MultiTenancyPrincipal ip = new MultiTenancyPrincipal(mtu);
			return ip;

		} catch (Exception e) {
			if (trace)
				log.trace("Query returned no matches from db");
			throw new FailedLoginException(ErrorCodes.PROCESSING_FAILED
					+ "No matching username found in Principals");
		}
	}

	/**
	 * Execute the rolesQuery against the dsJndiName to obtain the roles for the
	 * authenticated user.
	 * 
	 * @return Group[] containing the sets of roles
	 */
	protected Group[] getRoleSets() throws LoginException {
		Principal identity = getIdentity();

		SimpleGroup group = new SimpleGroup("Roles");

		if (!(identity instanceof MultiTenancyPrincipal))
			return super.getRoleSets();

		for (MultiTenancyUserRole role : ((MultiTenancyPrincipal) identity)
				.getUserRoles()) {
			group.addMember(new SimplePrincipal(role.getRole()));
		}

		return new Group[] { group };
	}
}
