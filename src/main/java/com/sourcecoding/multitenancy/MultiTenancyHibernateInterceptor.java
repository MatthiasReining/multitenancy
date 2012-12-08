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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;


/**
 * @author matthias reining
 */
public class MultiTenancyHibernateInterceptor extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;

	private static final String TENANT_ID_COLUMN = "tenantid";

	@Override
	public String onPrepareStatement(String sql) {

		MultiTenancyService mtService = ManagedObject
				.ejbLookup(MultiTenancyService.class);
		MultiTenancyPrincipal mtp = mtService.getPrincipal();

		if (mtp == null)
			return sql;

		if (sql.contains("insert into"))
			return sql;

		for (String mto : mtService.getAllMultiTenancyTableNames()) {
			if (sql.contains(mto)) {

				String client = mtp.getTenant();

				Matcher matcher = Pattern.compile(mto + " (\\w+)").matcher(sql);
				// Check all occurance
				matcher.find(); // always result, because of contains check
				String tableAlias = matcher.group(1);

				StringBuffer sb = new StringBuffer(" where ");
				sb.append(tableAlias).append(".").append(TENANT_ID_COLUMN)
						.append("=");
				sb.append("'").append(client).append("' ");

				if (sql.contains("where")) {
					// only add tenant where clause if not exists
					String test = sql.substring(sql.indexOf(" where "));
					if (!test.contains(TENANT_ID_COLUMN))
						sql = sql.replaceFirst(" where ", sb.toString()
								+ " and ");
				} else
					sql = sql + sb.toString();
			}
		}

		System.out.println("in meinem Interceptor: " + sql);
		return sql;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {

		if (entity instanceof MultiTenancyEntity) {
			String client = ManagedObject.ejbLookup(MultiTenancyService.class)
					.getPrincipal().getTenant();

			for (int i = 0; i < propertyNames.length; i++) {
				if ("tenantid".equals(propertyNames[i])) {
					if (client.equals(currentState[i]))
						return false;
					currentState[i] = client;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		if (entity instanceof MultiTenancyEntity) {
			String client = ManagedObject.ejbLookup(MultiTenancyService.class)
					.getPrincipal().getTenant();
			for (int i = 0; i < propertyNames.length; i++) {
				if ("tenantid".equals(propertyNames[i])) {
					if (client.equals(state[i]))
						return false;
					state[i] = client;
					return true;
				}
			}
		}
		return false;
	}

}
