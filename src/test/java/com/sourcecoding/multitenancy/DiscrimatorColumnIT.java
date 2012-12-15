package com.sourcecoding.multitenancy;

import java.util.Date;

import javax.inject.Inject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.sourcecoding.multitenancy.model.SampleConfiguration;
import com.sourcecoding.multitenancy.security.CRUDTestService;
import com.sourcecoding.multitenancy.security.JBossLoginContextFactory;

@RunWith(Arquillian.class)
public class DiscrimatorColumnIT {
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
	CRUDTestService crud;

	@Test
	public void persisteMultitenancyObject() throws LoginException {
		LoginContext loginContext = JBossLoginContextFactory
				.createLoginContext(TENANT, USERNAME, PASSWORD);
		loginContext.login();

		SampleConfiguration sc = new SampleConfiguration();
		sc.setTest("blub");

		sc = crud.create(sc);
		System.out.println(sc.getId() + " " + " " + sc.getTest() + " "
				+ sc.getTenantId());

	}

}
