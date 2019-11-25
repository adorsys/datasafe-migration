This is tool for a silent migration of datasafe.

![Modules map](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/datasafe-migration/develop/docs/diagrams/silent-migration.puml&fmt=svg&vvv=1&sanitize=true)

Datasafe 1.0.0 has changed path encryption and keystore type. For this it is no more compatible to datasafe 0.6.1.
As user data can only be migrated with the users password, a silent migration is needed. To achieve this, this migration component has been build.

This component behaves like datasafe 1.0.0, e.g. the Interface looks completly the same.
But the classes provided here are only wrapper classes. Specially the implementation of the SimpleDatasafeService.
This is no more a SimpleDatasafeServiceImpl, but now it is a SimpleDatasafeServiceWithMigration. As it is injected by Spring,
this is transparent for the user.

The component works like this, as shown in the diagramm above: 

For ANY call to the SimpleDatasafeService first it is checked, if the user 
is already migrated or not. If not, the internally available Datasafe 0.6.1 DatasafeServiceImpl is used
to read all the data. Than the internally available Datasafe 1.0.0 DatasafeServiceImpl is used to 
store all the users data. By doing this, the key store is recreated with the 
same password but a different type (now it is a BCFKS rather that an UBER).

When the migration is done successfully an additional file is created directly in the 
root of the user. This file is not encrypted and even the pathname is not encrypted.
This file simply tells that the user is in the new format of datasafe 1.0.0.

# modules
## datasafe-migration-shaded-0.6.1
contains the datasafe version 0.6.1. All classes have the path <code>de.adorsys.datasafe_0_6_1</code>
 instead of <code>de.adorsys.datasafe</code>. Further the used classes of datasafe have the prefix 
 <code>S061</code>.
## datasafe-migration-shaded-1.0.0
contains the datasafe version 1.0.0. All classes have the path <code>de.adorsys.datasafe_1_0_0</code>
 instead of <code>de.adorsys.datasafe</code>. Further the used classes of datasafe have the prefix 
 <code>S100</code>.
## datasafe-migration
contains the classes, which actually do the migration itself, e.g. read all files
from the <code>S061_SimpledatasafeAdapter</code> and write it to the <code>S100_SimpleDatasafeAdapter</code>.
## datasafe-migration-silent
contains all the classes of the SimpleDatasafeAdapter. These classes are wrapper. The Service class
SimpleDatasafeService is an instance of SimpleDatasafesServiceWithMigration. That class itself
contains the old and the new SimpleDatasafeAdapterImpls (e.g. <code>S061_SimpleDatasafeAdapterServiceImpl</code>
and <code>S100_SimpleDatasafeAdapterServiceImpl</code>).

 
# Development
## shading
To be sure which class of which jar (shaded 0.6.1, shaded 1.0.0) is used 
all directly used shaded classes got a prefix
```
S061_SimpleDatasafeService oldService...
S100_SimpleDatasafeService newService...
```
The new classes, which wrap the old classes look like the interface of
Datasafe 1.0.0.

```
SimpleDatasafeService simpleDatasafeService ....
``` 

To make sure, that the old and the current datasafe adapter are not imported, they are
explicitly excluded each time, the shaded jars are included. With  this, all
the implicit inclusions of third libraries like bouncy castle, lombok and so on
were lost too and had to be included manually too.

## IDE

If you use intellij and might have problems in finding shaded classes do
```
File -> Invalidate Caches / Restart
```

If you still have problems do
```
mvn clean install -DskipTests
select pom.xml of datasafe-migration-shaded-xxx project
right click -> Maven -> ignore Projects
select root pom.xml
right click -> Maven -> reimport
``` 





