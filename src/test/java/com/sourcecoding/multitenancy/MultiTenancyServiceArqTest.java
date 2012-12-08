package com.sourcecoding.multitenancy;

import java.util.Date;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.sourcecoding.multitenancy.model.SampleUser;
import com.sourcecoding.multitenancy.model.SampleUserRole;
import com.sourcecoding.multitenancy.security.CRUDTestService;
import com.sourcecoding.multitenancy.security.JBossLoginContextFactory;

@RunWith(Arquillian.class)
public class MultiTenancyServiceArqTest {
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "geheim";
	private static final String TENANT = "SAMPLE";

	static Date actionTime;

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

	@Inject
	CRUDTestService crudService;

	@Before
	public void prepareTest() {
		actionTime = new Date();
	}

	public void listenToAuthenticationSuccessful(
			@Observes @AuthenticationSuccessful AuthenticationEvent ae) {
		System.out.println("in listenToAuthenticationSuccessful");
		SampleUser u = (SampleUser) ae.getUser();
		u.setLastAccess(actionTime);
		u.setLastLogin(actionTime);
		u.setLoginFailed(0);
	}

	public void listenToAuthenticationFailed(
			@Observes @AuthenticationFailed AuthenticationEvent ae) {

		SampleUser u = (SampleUser) ae.getUser();
		u.setLastAccess(actionTime);
		u.setLoginFailed(u.getLoginFailed() + 1);
	}

	@Test
	@InSequence(10)
	public void shouldAuthenticate() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory
				.createLoginContext(TENANT, USERNAME, PASSWORD);
		loginContext.login();

		SampleUser su = (SampleUser) mts.getPrincipal().getUser();

		Assert.assertEquals(USERNAME, mts.getPrincipal().getName());
		Assert.assertEquals(actionTime, su.getLastLogin());
		Assert.assertEquals(actionTime, su.getLastAccess());
		Assert.assertEquals(0, su.getLoginFailed());

		loginContext.logout();
	}

	@Test
	@InSequence(20)
	public void shouldNotAuthenticate() throws LoginException {
		try {
			LoginContext loginContext = JBossLoginContextFactory
					.createLoginContext(TENANT, USERNAME, "wrong-password");
			loginContext.login();
		} catch (LoginException le) {
			// do nothing
		}
		try {
			LoginContext loginContext = JBossLoginContextFactory
					.createLoginContext(TENANT, USERNAME, "wrong-password");
			loginContext.login();
		} catch (LoginException le) {
			// do nothing
		}

		MultiTenancyUser mu = mts.getMultiTenancyUser(TENANT, USERNAME);

		SampleUser su = (SampleUser) mu;

		Assert.assertEquals(USERNAME, su.getUsername());
		Assert.assertNotSame(actionTime, su.getLastLogin());
		Assert.assertEquals(actionTime, su.getLastAccess());
		Assert.assertEquals(2, su.getLoginFailed());

	}

	@Test
	@InSequence(30)
	public void shouldAuthenticateAgain() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory
				.createLoginContext(TENANT, USERNAME, PASSWORD);
		loginContext.login();

		SampleUser su = (SampleUser) mts.getPrincipal().getUser();

		Assert.assertEquals(USERNAME, mts.getPrincipal().getName());
		Assert.assertEquals(actionTime, su.getLastLogin());
		Assert.assertEquals(actionTime, su.getLastAccess());
		Assert.assertEquals(0, su.getLoginFailed());

		loginContext.logout();
	}

	@Test
	@InSequence(40)
	public void shouldCreateNewUser() throws LoginException {
		String newUsername = "Newbe";
		String newPassword = "secret";

		SampleUser su = new SampleUser();
		su.setTenant(TENANT);
		su.setUsername(newUsername);
		su.setPassword(SecurityTools.createHash(newPassword));
		SampleUserRole sur = new SampleUserRole();
		sur.setRole("READER");
		sur.setMultiTenancyUser(su);
		su.getRoles().add(sur);

		crudService.create(su);

		LoginContext loginContext = JBossLoginContextFactory
				.createLoginContext(TENANT, newUsername, newPassword);
		loginContext.login();

		MultiTenancyPrincipal mtp = (MultiTenancyPrincipal) mts.getPrincipal();

		Assert.assertEquals(newUsername, mtp.getName());
		Assert.assertEquals(1, mtp.getUserRoles().size());

		loginContext.logout();

	}

}
