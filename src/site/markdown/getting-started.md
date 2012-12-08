1. Download
===========

Maven Users
-----------

If you are using Maven, simply copy the following dependency into your pom.xml file. 
The artifact is hosted at [Maven Central Repository](http://search.maven.org/#search%7Cga%7C1%7Ccom.sourcecoding.multitenancy).

	<dependency>
	    <groupId>com.sourcecoding</groupId>
	    <artifactId>multitenancy</artifactId>
	    <version>0.3.2</version>
	</dependency>

JBoss AS 7 (picketbox) is an dependency of multitenancy. Unfortunately picketpox is not in maven central repository, so you have
to add the JBoss repository in your pom.xml

	<repository>
		<id>jboss-releases-repository</id>
		<name>JBoss Releases Repository</name>
		<url>http://repository.jboss.org/nexus/content/groups/public-jboss/</url>
	</repository>


Everyone else
-------------

Start using maven ;-)

But you can also easily download the jar File direct 
from [Maven Central Repository](http://search.maven.org/#browse%7C-913672894)
and put it your WEB-INF\lib Folder. There's no *direct* dependencies if you use 
multitenancy4jee with JBoss AS 7.

You will find multitenancy4jee as source, javadoc and jar-package.
  
  
  


<a name="multitenancy-authentication"/>
2. Multitenancy Authentication
==============================

Die multitenancy4jee Bibliothek ermöglicht die Authentifizierung eines Benutzer 
mit Benutzernamen, Passworrt und Mandant.

Authentication Concept
----------------------

Dies wird ermöglicht durch die Verwendung des `MultiTenancyPrincipal`. Dieser 
basiert und erweitert die Klasse `org.jboss.security.SimplePrincipal` um ein 
Mandanten Attribut (tenant).

Aktuell wird lediglich eine datenbankbasierte Benutzerverwaltung unterstüzt. 
Hierzu muss eine JPA User-Entität das Interface `MultiTenancyUser` implementieren 
und eine JPA Role-Entität das Interface `MultiTenancyUserRole`.

Um die Verwendung zu vereinfachen existieren hierzu bereits die abstrakten 
JPA Entitäten `AbstractMultiTenancyUser` und `AbstractMultiTenancyUserRole` 
(jeweils gekennzeichnet mit `@MappedSuperclass`).


<a name="multitenancy-authentication-entity"/>
Authentication Entity
---------------------

Die multitenancy4jee Bibliothek definiert nicht direkt die Datenbankentitäten. 
Damit ist es möglich die Entitäten selbst zu definieren inklusive weiterer 
zusätzlicher Attribute wie bspw. created, lastLogin, loginFailed usw.

Folgende Beispiele zeigt, was notwendig ist um die beiden Entiäten zu definieren:


			
Beispielentität **User**
			
	package de.mreining.example.model;
	
	import java.util.List;
	
	import javax.persistence.CascadeType;
	import javax.persistence.Entity;
	import javax.persistence.GeneratedValue;
	import javax.persistence.Id;
	import javax.persistence.OneToMany;
	
	import com.sourcecoding.multitenancy.AbstractMultiTenancyUser;
	import com.sourcecoding.multitenancy.MultiTenancyUser;
	import com.sourcecoding.multitenancy.MultiTenancyUserRole;
	
	@Entity
	public class MyUser extends AbstractMultiTenancyUser implements MultiTenancyUser {
	
		private static final long serialVersionUID = 1L;
	
		@Id
		@GeneratedValue
		private Long id;
	
		@OneToMany(mappedBy = "multiTenancyUser", cascade = CascadeType.ALL)
		private List<MyUserRole> roles;
	
		public Long getId() {
			return id;
		}
	
		public void setId(Long id) {
			this.id = id;
		}
	
		@Override
		public List<MyUserRole> getRoles() {
			return roles;
		}
			
		@SuppressWarnings("unchecked")
		@Override
		public void setRoles(List<? extends MultiTenancyUserRole> roles) {
			this.roles = (List<MyUserRole>) roles;
		
		}
	}


Beispielentität **UserRole**

	package de.mreining.example.model;
	
	import javax.persistence.Entity;
	import javax.persistence.GeneratedValue;
	import javax.persistence.Id;
	import javax.persistence.ManyToOne;
	
	import com.sourcecoding.multitenancy.AbstractMultiTenancyUserRole;
	import com.sourcecoding.multitenancy.MultiTenancyUser;
	
	@Entity
	public class MyUserRole extends AbstractMultiTenancyUserRole {
	
		private static final long serialVersionUID = 1L;
	
		@Id
		@GeneratedValue
		private Long id;
	
		@ManyToOne
		private MyUser myTenancyUser;
	
		public Long getId() {
			return id;
		}
	
		public void setId(Long id) {
			this.id = id;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public <T extends MultiTenancyUser> T getMultiTenancyUser() {
			return (T) multiTenancyUser;
		}
	
		@Override
		public <T extends MultiTenancyUser> void setMultiTenancyUser(T multiTenancyUser) {
			this.multiTenancyUser = (ItomUser) multiTenancyUser;
		}
	}

<a name="activate-multitenancy-principal" />

Activate Multitenancy Principal
-------------------------------

Um die mandantenfähige Benutzerverwaltung "scharf" zu schalten, muss beim JBoss AS 7 folgender
Eintrag in der *standalone.xml* vorgenommen werden:

	<subsystem xmlns="urn:jboss:domain:security:1.1">
		<security-domains>
			<security-domain name="my-server" cache-type="default">
				<authentication>
					<login-module code="com.sourcecoding.multitenancy.MultiTenancyDatabaseServerLoginModule" flag="required"/>
				</authentication>
			</security-domain>
		<security-domain ...
	     ...

Das Gegenstück ist hier die JBoss spezifische Konfigurationdatei jboss-web.xml im 
Projektverzeichnis (&lth;project-root&gth;/src/main/webapp/WEB-INF/jboss-web.xml).


	<?xml version="1.0" encoding="UTF-8"?>
		
	<jboss-web>
		<security-domain>java:/jaas/my-server</security-domain>
	</jboss-web>


3. Multitenancy Content
=======================

The autentication is done. There are severaly oportunities to store data by tenant.
A very cood description is on the microsoft page (MSDN): [Multi-Tenant Data Architecture](http://msdn.microsoft.com/en-us/library/aa479086.aspx)
Furthermore gives the hibernate page explantion: [Hibernate Multi-tenancy](http://docs.jboss.org/hibernate/orm/4.1/devguide/en-US/html/ch16.html).

Multitenancy4JEE supports multi tenant by using a discriminator column.

Hibernate maybe will implement this in Version 5.

> Correlates to the partitioned (discriminator) approach. It is an error to attempt 
> to open a session without a tenant identifier using this strategy. This strategy is 
> not yet implemented in Hibernate as of 4.0 and 4.1. Its support is planned for 5.0.

So long Multitenancy4JEE gives a proprietary solutions for this:

To use Multitenancy your entity class has easily implement `MultiTenancyEntity`. You can also
extends your entity class with `AbstractMultiTenancyEntity`.

The rest is done from Multitenancy4JEE. You have not to regard the tenant-column in your
queries (or in your updates/inserts).
The current logged-in user respectively tenant is used for this.


     