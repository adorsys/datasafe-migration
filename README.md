This is tool for a silent migration of datasafe.

# purpose  
![Modules map](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/datasafe-migration/develop/docs/diagrams/silent-migration.puml&fmt=svg&vvv=1&sanitize=true)

Datasafe 1.0.1 has changed path encryption and keystore type. For this it is no more compatible to datasafe 0.6.1.
As user data can only be migrated with the users password, a silent migration is needed. To achieve this, this migration component has been build.

This component behaves like datasafe 1.0.1, e.g. the Interface looks completly the same.
But the classes provided here are only wrapper classes. Specially the implementation of the SimpleDatasafeService.
This is no more a SimpleDatasafeServiceImpl, but now it is a SimpleDatasafeServiceWithMigration. As it is injected by Spring,
this is transparent for the user.

The component works like this, as shown in the diagramm above: 

For ANY call to the SimpleDatasafeService first it is checked, if the user 
is already migrated or not. If not, the internally available Datasafe 0.6.1 DatasafeServiceImpl is used
to read all the data. Than the internally available Datasafe 1.0.1 DatasafeServiceImpl is used to 
store all the users data. By doing this, the key store is recreated with the 
same password but a different type (now it is a BCFKS rather that an UBER).

When the migration is done successfully an additional file is created directly in the 
root of the user. This file is not encrypted and even the pathname is not encrypted.
This file simply tells that the user is in the new format of datasafe 1.0.1.

# usage

## depenency

```
        <dependency>
            <groupId>de.adorsys</groupId>
            <artifactId>datasafe-migration-silent</artifactId>
            <version>1.0.1</version>
        </dependency>
```
## interface change
Rather than using the new Datasafe 1.0.1 you simple use Datasafe-Migration 1.0.1. As in Datasafe
1.0.1 the interface of the ReadKeyPassword has slightly changed, all occurrences of 
ReadKeyPassword have to be changed.

```
# 0.6.1
import de.adorsys.datasafe.encrypiton.api.types.keystore.ReadKeyPassword;
UserIDAuth userIDAuth = new UserIDAuth(new UserID("peter"), new ReadKeyPassword("password"));
```


```
# 1.0.1
import de.adorsys.datasafe.types.api.types.ReadKeyPassword;
UserIDAuth userIDAuth = new UserIDAuth(new UserID("peter"), new ReadKeyPassword("password"::toCharArray));
``` 

As the migration completly behaves like the new datasafe, these interface changes have to be done.

## configuration

By default the migration is switched off, e.g. it behaves like datasafe 0.6.1 and does not do anything.
Anyway it has already the new interface for the ReadKeyPassword, even if the old datasafe logic is used.

To activate the migration logic the configuration.yml has to get jdbc parameters. As the migration must only be 
done on one server at the time, others servers have to be blocked for this user. For that shedlock is used.
And shedlock has to be provided with a database connection.

Currently three jdbc implementations are integrated and simply can be switched on, by setting the follogin
parameters in the <code>configuration.yml</code>. 

### H2SQL
```
datasafe
  migration:
    lockprovider:
      jdbc:
        hikari:
          url: jdbc:hsqldb:hsql://localhost/test
          username: SA
```

### MYSQL
```
datasafe
  migration:
    lockprovider:
      jdbc:
        mysql:
          url: jdbc:mysql://localhost:3333/shedlock
          username: root
          password: my-secret-pw
```
### POSTGRES
```
datasafe
  migration:
    lockprovider:
      jdbc:
        postgres:
          url: jdbc:postgresql://localhost:5432/shedlock
          username: postgres
          password: password
```

By default, the migration migrates the users from the existing DFS directory to itself. To do this,
a temporary subfolder with name <code>tempForMigrationTo100</code> is used which is below the root directory.
After a successfull migration this directory always is empty, as the user has been moved back to the original
directory.

If you dont want the users to be moved back, you can use the option

```
datasafe
  migration:
    distinctfolder: true 
```
Then all migrated users go to the subfolder <code>tempForMigrationTo100</code> and stay there.

## check configuration

When the migration is disabled (e.g. no migration parameters given) the log writes 
```
************************************
*                                  *
*  MigrationLogic      : DISABLED  *
*                                  *
************************************
```
When the migration is enabeld the log may look like:

```
******************************************************************************
*                                                                            *
*  MigrationLogic      : ENABLED                                             *
*    migration timeout : 20000                                               *
*  intermediate folder : YES                                                 *
*             old root : s3://adorsys-test-migration/                        *
*    intermediate root : s3://adorsys-test-migration/tempForMigrationTo100/  *
*             new root : s3://adorsys-test-migration/                        *
*                                                                            *
******************************************************************************
```
or, if <code>distinctfolder: true</code> has been set it could be: 
```
************************************************************
*                                                          *
*  MigrationLogic      : ENABLED                           *
*    migration timeout : 20000                             *
*  intermediate folder : NO                                *
*             old root : s3://adorsys-test-migration/      *
*             new root : s3://adorsys-test-migration/100/  *
*                                                          *
************************************************************
```

## timeout
The migration should not take longer than a second. But this strongly depends on the 
s3 connection, the hardware and the amount of data, that has to be migrated. 
For that a user, that tries to access the data is blocked as long as the migration is active. 
If a user at another node tries to access the same user, this request becomes blocked too. As soon 
as the migration is finished, the block is release. But if for WHATEVER reasons, the
migration takes longer as excpeted, an exception is thrown and the migration aborted.
This time, to wait for a migration to be finished is set in the default to 20 seconds and
can be set in the properties too:
```
datasafe
  migration:
    timeout: 20000
```

## log
For each user a log is written:
```
10:51:14.894 [main] INFO de.adorsys.datasafemigration.MigrationLogic - MIGRATION OF 21 FILES FOR USER user_1 TOOK 1843 MILLIS. Migration itself took 1588 millis and relocation of files took 255 millis.

```

# modules
## datasafe-migration-shaded-0.6.1
contains the datasafe version 0.6.1. All classes have the path <code>de.adorsys.datasafe_0_6_1</code>
 instead of <code>de.adorsys.datasafe</code>. Further the used classes of datasafe have the prefix 
 <code>S061_</code>.
## datasafe-migration-shaded-1.0.1
contains the datasafe version 1.0.1. All classes have the path <code>de.adorsys.datasafe_1_0_3</code>
 instead of <code>de.adorsys.datasafe</code>. Further the used classes of datasafe have the prefix 
 <code>S103_</code>.
## datasafe-migration
contains the classes, which actually do the migration itself, e.g. read all files
from the <code>S061_SimpledatasafeAdapter</code> and write it to the <code>S103_SimpleDatasafeAdapter</code>.
## datasafe-migration-silent
contains all the classes of the SimpleDatasafeAdapter. These classes are wrapper. The Service class
SimpleDatasafeService is an instance of SimpleDatasafesServiceWithMigration. That class itself
contains the old and the new SimpleDatasafeAdapterImpls (e.g. <code>S061_SimpleDatasafeAdapterServiceImpl</code>
and <code>S103_SimpleDatasafeAdapterServiceImpl</code>).

 
# Development
## shading
To be sure which class of which jar (shaded 0.6.1, shaded 1.0.1) is used 
all directly used shaded classes got a prefix
```
S061_SimpleDatasafeService oldService...
S103_SimpleDatasafeService newService...
```
The new classes, which wrap the old classes look like the interface of
Datasafe 1.0.1.

```
SimpleDatasafeService simpleDatasafeService ....
``` 

To make sure, that the old and the current datasafe adapter are not imported, they are
explicitly excluded. With  this, all
the implicit inclusions of third libraries like bouncy castle, lombok and so on
were lost too and had to be included manually too. But all this is none of the business of users of the 
datasafe-migration-silent.jar.

## IDE

If you use intellij and might have problems in finding shaded classes do
```
File -> Invalidate Caches / Restart
```

If you still have problems do
```
mvn clean install -DskipTests
select pom.xml of datasafe-migration-shaded-061 project
right click -> Maven -> ignore Projects
select pom.xml of datasafe-migration-shaded-101 project
right click -> Maven -> ignore Projects
select root pom.xml
right click -> Maven -> reimport
``` 





