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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;

/**
 * @author matthias reining
 */
@Stateless
@Startup
public class MultiTenancyService {

	@PersistenceContext
	EntityManager em;

	@Resource
	private SessionContext sctx;

	@Inject
	@AuthenticationSuccessful
	Event<AuthenticationEvent> authenticationSuccessfulEvent;

	@Inject
	@AuthenticationFailed
	Event<AuthenticationEvent> authenticationFailedEvent;

	private List<String> multiTenancyObjects = new ArrayList<>();

	private String userTableEntityName;

	private String userRoleTableEntityName;

	@PostConstruct
	public void init() {
		System.out.println("Initalize Multitenancy entities");
		long time = System.currentTimeMillis();

		List<String> list = new ArrayList<>();

		for (EntityType<?> et : em.getMetamodel().getEntities()) {
			Class<?> c = et.getJavaType();
			if (c.getAnnotation(Entity.class) != null) {

				Object o;
				try {
					o = c.newInstance();
					if (o instanceof MultiTenancyEntity) {
						String tablename = c.getSimpleName();
						if (c.isAnnotationPresent(Table.class))
							tablename = c.getAnnotation(Table.class).name();
						System.out.println("Multitenancy class: " + c.getName()
								+ " / tablename: " + o);
						list.add(tablename);
					}
					if (o instanceof MultiTenancyUser) {
						if (userTableEntityName != null)
							throw new RuntimeException(
									"The MultiTenancyUser can only have one instance.");
						userTableEntityName = c.getSimpleName();
					}
					if (o instanceof MultiTenancyUserRole) {
						if (userRoleTableEntityName != null)
							throw new RuntimeException(
									"The MultiTenancyUserRole can only have one instance.");
						userRoleTableEntityName = c.getSimpleName();
					}

				} catch (InstantiationException | IllegalAccessException e) {
					// nothing to do
				}
			}
		}

		if (userTableEntityName == null || userRoleTableEntityName == null)
			throw new RuntimeException(
					"There has to be an instance of (Abstract)MultiTenancyUserRole and (Abstract)MultTenancyiUserRole");

		multiTenancyObjects = Collections.unmodifiableList(list);
		System.out.println("dauer: " + (System.currentTimeMillis() - time)
				+ "ms");

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public MultiTenancyUser getMultiTenancyUser(String tenant, String name) {

		MultiTenancyUser mtu = (MultiTenancyUser) em
				.createQuery(
						"select m from " + userTableEntityName
								+ " m where m.tenant = ? and m.username = ?")
				.setParameter(1, tenant).setParameter(2, name)
				.getSingleResult();
		// eager loading
		if (mtu == null)
			return null;
		mtu.getRoles().size();
		return mtu;
	}

	public MultiTenancyPrincipal getPrincipal() {
		Principal p = sctx.getCallerPrincipal();

		if (p instanceof MultiTenancyPrincipal)
			return (MultiTenancyPrincipal) p;
		return null;
	}

	/**
	 * All multi tenancy tables.
	 * 
	 * @return list of all tables with interface {@link MultiTenancyEntity}
	 */
	public List<String> getAllMultiTenancyTableNames() {
		return multiTenancyObjects;
	}

	public List<? extends MultiTenancyUser> getMultiTenancyUserList(
			String tenant) {
		@SuppressWarnings("unchecked")
		List<AbstractMultiTenancyUser> mtuList = (List<AbstractMultiTenancyUser>) em
				.createQuery(
						"select m from " + userTableEntityName
								+ " m where m.tenant = ?")
				.setParameter(1, tenant).getResultList();

		for (MultiTenancyUser mtu : mtuList)
			mtu.getRoles().size();

		return mtuList;

	}

	public void handleAuthentication(MultiTenancyPrincipal principal,
			boolean authenticated) {

		MultiTenancyUser mu = em.merge(principal.getUser());
		AuthenticationEvent ae = new AuthenticationEvent(mu, authenticated);

		if (authenticated)
			authenticationSuccessfulEvent.fire(ae);
		else
			authenticationFailedEvent.fire(ae);

		principal.mtu = mu;

		System.out.println("nach fire");

	}
}
