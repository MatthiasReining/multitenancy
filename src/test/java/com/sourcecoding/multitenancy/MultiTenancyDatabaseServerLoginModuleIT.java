package com.sourcecoding.multitenancy;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.sourcecoding.multitenancy.security.JBossLoginContextFactory;

@RunWith(Arquillian.class)
public class MultiTenancyDatabaseServerLoginModuleIT {

	private static final String USERNAME = "admin";
	private static final String PASSWORD = "geheim";
	private static final String TENANT = "SAMPLE";

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
				.addClass(JBossLoginContextFactory.class)
				.addPackages(true, "com.sourcecoding.multitenancy")
				.addAsResource("META-INF/persistence.xml")
				.addAsResource("import.sql")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(jar.toString(true));
		return jar;
	}

	@Inject
	MultiTenancyService mts;

	@Test
	public void type() throws Exception {
		assertThat(MultiTenancyDatabaseServerLoginModule.class, notNullValue());
	}

	@Test
	public void shouldLogin() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory
				.createLoginContext(TENANT, USERNAME, PASSWORD);
		loginContext.login();

		Assert.assertEquals(USERNAME, mts.getPrincipal().getName());
		Assert.assertEquals(TENANT, mts.getPrincipal().getTenant());

		loginContext.logout();
	}

	@Test(expected = LoginException.class)
	public void shouldNotAuthenticateWrongPW() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory
				.createLoginContext(TENANT, USERNAME, "wrong-pw");
		loginContext.login();
	}

	@Test(expected = LoginException.class)
	public void shouldNotAuthenticateWrongTenant() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory
				.createLoginContext("NOT_EXISTS", USERNAME, PASSWORD);
		loginContext.login();
	}

	@Test(expected = LoginException.class)
	public void shouldNotAuthenticateWrongUsername() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory
				.createLoginContext(TENANT, "no-user", PASSWORD);
		loginContext.login();
	}

}
