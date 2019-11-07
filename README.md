This is tool for a silent migration of datasafe.

![Modules map](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/datasafe-migration/develop/docs/diagrams/silent-migration.puml&fmt=svg&vvv=1&sanitize=true)


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

# Details
To get rid of long pathes like 
```
import de.adorsys.datasafe_0_7_1.simple.adapter.api.SimpleDatasafeService
...
de.adorsys.datasafe_0_6_1.simple.adapter.api.SimpleDatasafeService oldService...
SimpleDatasafeService newService...
```
all directly used old classes got prefix SO_ (Shaded Old) to better see in import section what is used
```
import de.adorsys.datasafe_0_6_1.simple.adapter.api.SO_SimpleDatasafeService
import de.adorsys.datasafe_0_7_1.simple.adapter.api.SimpleDatasafeService
...
SO_SimpleDatasafeService oldService...
SimpleDatasafeService newService...
```



