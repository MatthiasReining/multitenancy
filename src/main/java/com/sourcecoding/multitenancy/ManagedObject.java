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

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Utility class for EJBs. There's a {@link #lookup(Class)} method which allows
 * you to lookup the current instance of a given EJB class from the JNDI
 * context. This utility class assumes that EJBs are deployed in the WAR as you
 * would do in Java EE 6 Web Profile. For EARs, you'd need to alter the
 * <code>EJB_CONTEXT</code> to add the EJB module name or to add another
 * lookup() method.
 * 
 * @see <a
 *      href="http://balusc.blogspot.com/2011/09/communication-in-jsf-20.html#GettingAnEJBInFacesConverterAndFacesValidator">http://balusc.blogspot.com/2011/09/communication-in-jsf-20.html#GettingAnEJBInFacesConverterAndFacesValidator</a>
 * 
 * @author matthias reining
 */
public final class ManagedObject {

	private ManagedObject() {
		// Utility class, so hide default constructor.
	}

	/**
	 * Lookup the current instance of the given EJB class from the JNDI context.
	 * If the given class implements a local or remote interface, you must
	 * assign the return type to that interface to prevent ClassCastException.
	 * No-interface EJB lookups can just be assigned to own type. E.g. <li>
	 * <code>IfaceEJB ifaceEJB = EJB.lookup(ConcreteEJB.class);</code> <li>
	 * <code>NoIfaceEJB noIfaceEJB = EJB.lookup(NoIfaceEJB.class);</code>
	 * 
	 * @param <T>
	 *            The EJB type.
	 * @param ejbClass
	 *            The EJB class.
	 * @return The instance of the given EJB class from the JNDI context.
	 * @throws IllegalArgumentException
	 *             If the given EJB class cannot be found in the JNDI context.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T ejbLookup(Class<T> ejbClass) {
		// XXX comment-by-mre: java:module dürfte nur auf dem JBoss AS7 funzen.
		// Bei anderme APP Server muss man hier nochmal ran.
		// Eine Möglichkeit ist hier beschrieben:
		// http://stackoverflow.com/questions/2088218/how-can-i-get-the-name-of-the-current-web-app-in-j2ee
		// aber für den Moment dürfte das hier so reichen ;-)
		String jndiName = "java:module/" + ejbClass.getSimpleName();

		try {
			// Do not use ejbClass.cast(). It will fail on local/remote
			// interfaces.
			return (T) new InitialContext().lookup(jndiName);
		} catch (NamingException e) {
			throw new IllegalArgumentException(String.format(
					"Cannot find EJB class %s in JNDI %s", ejbClass, jndiName),
					e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T cdiLookup(Class<T> clazz) {
		BeanManager bm = getBeanManager();
		String beanName = clazz.getSimpleName();
		System.out.println("cdiLookup for " + beanName);
		// Set<Bean<?>> beans = bm.getBeans(clazz);
		Set<Bean<?>> beans = bm.getBeans(clazz, new AnnotationLiteral<Any>() {
			private static final long serialVersionUID = 1L;
		});
		if (beans == null || beans.isEmpty()) {
			return null;
		}
		// List<Annotation> qualifierList = Arrays.asList(qualifier);
		Bean<?> bean = null;
		// Iterator<Bean<?>> it = beans.iterator();
		// while (it.hasNext()) {
		// bean = it.next();
		// boolean match = true;
		// for (Annotation a : bean.getQualifiers()) {
		// if (!qualifierList.contains(a)) {
		// match = false;
		// break;
		// }
		// }
		// if (match)
		// break;
		// }
		// if (bean == null)
		bean = beans.iterator().next();

		if (beans.iterator().hasNext()) {
			System.out
					.println("WARNING, Qualifiers are at the moment not considered!");
		}

		CreationalContext<?> ctx = bm.createCreationalContext(bean);
		Object o = bm.getReference(bean, bean.getClass(), ctx);

		return (T) o;
	}

	/**
	 * Lookup the current instance of BeanManager from the JNDI context.
	 * 
	 * @return The instance of the BeanManager
	 */
	private static BeanManager getBeanManager() {
		try {
			InitialContext initialContext = new InitialContext();
			return (BeanManager) initialContext.lookup("java:comp/BeanManager");
		} catch (NamingException e) {
			throw new RuntimeException("Couldn't find BeanManager in JNDI");
		}
	}

}