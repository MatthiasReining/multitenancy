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

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


/**
 * <p>
 * Add to your non-abstract class the dependencies to {@link MultiTenancyUserRole}<br/>
 * Use:
 * <pre>
 *  <b>&#64;OneToMany(mappedBy = "multiTenancyUser"</b>, cascade = CascadeType.ALL)
 *  <b>private List<&#63; extends MultiTenancyUserRole> roles</b>;
 * </pre>
 * </p>
 * 
 * @author matthias reining
 */
@MappedSuperclass
public abstract class AbstractMultiTenancyUser implements Serializable, MultiTenancyUser {

	private static final long serialVersionUID = 1L;

	@Column(unique=true)
	private String username;
	private String password;
	private String tenant;

	

	/* (non-Javadoc)
	 * @see com.sourcecoding.multitenancy.MultiTenancyUser#getUsername()
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/* (non-Javadoc)
	 * @see com.sourcecoding.multitenancy.MultiTenancyUser#setUsername(java.lang.String)
	 */
	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	/* (non-Javadoc)
	 * @see com.sourcecoding.multitenancy.MultiTenancyUser#getPassword()
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/* (non-Javadoc)
	 * @see com.sourcecoding.multitenancy.MultiTenancyUser#setPassword(java.lang.String)
	 */
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see com.sourcecoding.multitenancy.MultiTenancyUser#getTenant()
	 */
	@Override
	public String getTenant() {
		return tenant;
	}

	/* (non-Javadoc)
	 * @see com.sourcecoding.multitenancy.MultiTenancyUser#setTenant(java.lang.String)
	 */
	@Override
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	
}
