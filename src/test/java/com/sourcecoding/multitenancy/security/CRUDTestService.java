package com.sourcecoding.multitenancy.security;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CRUDTestService {

	@PersistenceContext
	EntityManager em;

	public <T> T create(T obj) {
		return em.merge(obj);
	}

	public <T> T update(T obj) {
		return em.merge(obj);
	}

}
