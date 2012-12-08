Welcome to Multitenancy 4 JavaEE
================================

What is Multitenancy 4 JavaEE?
------------------------------

**Authentication and data separation by tenant**

### Authentication

Multitenancy 4 Java EE extends the Java EE authentication by an tenant.
This allows you to make an authenticate on a JavaEE application
server with **tenant**, **username** and **password**.

**Current there's only support for JBoss AS 7.x!**   
(picket-box (`DatabaseServerLoginModule`) and hibernate is used).
The multitenancy authentication works only with a database realm (user information 
are stored in a RDBMS; LDAP/ActiveDirectory realm don't work because of the lack 
of a tenant in this authentication system) 

### Data separation by tenant

Furthermore your content can easily be signed as multi-tenant per table 
by using a discriminator column.

See [gettings started](getting-started.html). 


Latest News
-----------

###  next release 2012 - 0.4.2

* add google analytics to maven site

* add maven-fluido-skin (http://maven.apache.org/skins/maven-fluido-skin/)

* add maven site markdown syntax plugin (http://blog.akquinet.de/2012/04/12/maven-sites-reloaded/)

* start documentation ;-)


### October 2012 - Start

* Add login events 

* Starting with the first lines of code...