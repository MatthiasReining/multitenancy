Authentication
==============

To configure multitenancy see section [activate-multitenancy-principal](getting-started.html#activate-multitenancy-principal).

Entities
--------

A short explantion can you find at [Getting-Started-Authentication Entity](getting-started.html#multitenancy-authentication-entity).

Furthermore there are some unit-tests, who shows how you can implment 
your own entity model. Classes around `/src/test/java/com/sourcecoding/multitenancy/model/SampleUser.java`.

Password
--------

The MultiTenancyUser#password is stored as hash. You can create a hash by using com.sourcecoding.multitenancy.SecurityTools#createHash.
SecurityTools has also a main method to create a hash by command line:

	java -jar multitenancy-0.x.x.jar com.sourcecoding.multitenancy.SecurityTools password



Events
------

Multitenancy 4 JavaEE supports events!

If a user logged in an *authentication* event is triggered.
If an existing user tried to logged in but entered a wrong password also 
a special *authentication* event is triggered.

The following code snippets shows you how to use this events:

	public void listenToAuthenticationSuccessful(
			@Observes @AuthenticationSuccessful AuthenticationEvent ae) {
		SampleUser u = (SampleUser) ae.getUser();
		Date actionTime = new Date();
		u.setLastAccess(actionTime);
		u.setLastLogin(actionTime);
		u.setLoginFailed(0);
	}
	
	public void listenToAuthenticationFailed(
			@Observes @AuthenticationFailed AuthenticationEvent ae) {
		SampleUser u = (SampleUser) ae.getUser();
		Date actionTime = new Date();
		u.setLastAccess(actionTime);
		u.setLoginFailed(u.getLoginFailed() + 1);
	}

With the observer methods the authentication event can be handled (for further
CDI Information about @Observes see [Oracle: Using Events in CDI Applications](http://docs.oracle.com/javaee/6/tutorial/doc/gkhic.html).

The observer methods get a **JPA managed** user object. All modifications will be wrote to database
without any support. 

Sample
------

A full example how to use authentication events can be found as testcase.

see: 

* https://svn.java.net/svn/public-sourcecoding~scm/multitenancy/trunk/src/test/java/com/sourcecoding/multitenancy/MultiTenancyServiceArqTest.java

* https://svn.java.net/svn/public-sourcecoding~scm/multitenancy/trunk/src/test/java/com/sourcecoding/multitenancy/model/


