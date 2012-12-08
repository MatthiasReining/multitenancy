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

import javax.persistence.MappedSuperclass;

/**
 * <p>
 * Add to your non-abstract class the dependencies to {@link MultiTenancyUser}<br/>
 * Use:
 * <pre>
 *  <b>&#64;ManyToOne
 *  private MultiTenancyUser multiTenancyUser;
 * </pre>
 * </p>
 * 
 * @author matthias reining
 */
@MappedSuperclass
public abstract class AbstractMultiTenancyUserRole implements Serializable,
		MultiTenancyUserRole {

	private static final long serialVersionUID = 1L;

	private String role;

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sourcecoding.multitenancy.MultiTenancyUserRole#getRole()
	 */
	@Override
	public String getRole() {
		return role;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sourcecoding.multitenancy.MultiTenancyUserRole#setRole(java.lang.
	 * String)
	 */
	@Override
	public void setRole(String role) {
		this.role = role;
	}

}
