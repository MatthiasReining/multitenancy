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

import java.io.Serializable;
import java.security.Principal;
import java.util.List;

/**
 * @author matthias reining
 */
public class MultiTenancyPrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;
	protected final String name;
	protected final String tenant;
	protected final String ident;
	protected MultiTenancyUser mtu;

	public <T extends MultiTenancyUser> MultiTenancyPrincipal(T mtu) {
		name = mtu.getUsername();
		tenant = mtu.getTenant();
		ident = name + "/" + tenant;
		this.mtu = mtu;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Compare this MultiTenancyPrincipal's name against another Principal.
	 * 
	 * @return true if name equals another.getName();
	 */
	@Override
	public boolean equals(Object another) {
		if (!(another instanceof Principal))
			return false;
		if (!(another instanceof MultiTenancyPrincipal))
			return false;

		String anotherIdent = ((MultiTenancyPrincipal) another).ident;
		boolean equals = false;
		if (ident == null)
			equals = anotherIdent == null;
		else
			equals = ident.equals(anotherIdent);
		return equals;
	}

	@Override
	public int hashCode() {
		return (ident == null ? 0 : ident.hashCode());
	}

	@Override
	public String toString() {
		return ident;
	}

	public String getTenant() {
		return tenant;
	}

	public List<? extends MultiTenancyUserRole> getUserRoles() {
		return mtu.getRoles();
	}

	public <T extends MultiTenancyUser> MultiTenancyUser getUser() {
		return mtu;
	}

}
