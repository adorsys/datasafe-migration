This is tool for a silent migration of datasafe.

![Modules map](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/adorsys/datasafe-migration/develop/docs/diagrams/silent-migration.puml&fmt=svg&vvv=1&sanitize=true)


If you use intellij and might have problems in finding shaded classes do
```
File -> Invalidate Caches / Restart
```

If you still have problems do
```
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


# Unclear yet !
<details>
    <summary>To shade Datasafe 0.6.1 it is sufficiant to do:</summary>

```
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <minimizeJar>false</minimizeJar>
                            <keepDependenciesWithProvidedScope>false</keepDependenciesWithProvidedScope>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                            <createSourcesJar>false</createSourcesJar>

                            <artifactSet>
                                <includes>
                                    <include>de.adorsys:datasafe-simple-adapter-impl</include>
                                </includes>
                            </artifactSet>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```
</details>

All classes become shaded to directory datasafe_0_6_1 and the jar 
contains all datasafeclasses including indirect dependencies but no classes which are not
of datasafe project.

To shade Datasafe 0.7.1 the same configuration would lead to jar which is identically to the original
datasafe-simple-adapter-impl.jar

<details>
    <summary>To achieve the same results the configuration has to look like:</summary>

```    
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.2.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <minimizeJar>false</minimizeJar>
                            <keepDependenciesWithProvidedScope>false</keepDependenciesWithProvidedScope>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                            <createSourcesJar>false</createSourcesJar>

                            <artifactSet>
                                <includes>
                                    <include>de.adorsys:datasafe-simple-adapter-impl</include>
                                    <include>de.adorsys:datasafe-simple-adapter-api</include>
                                    <include>de.adorsys:datasafe-encryption-api</include>
                                    <include>de.adorsys:datasafe-business</include>
                                    <include>de.adorsys:datasafe-directory-api</include>
                                    <include>de.adorsys:datasafe-inbox-api</include>
                                    <include>de.adorsys:datasafe-privatestore-api</include>
                                    <include>de.adorsys:datasafe-storage-api</include>
                                    <include>de.adorsys:datasafe-types-api</include>
                                    <include>de.adorsys:datasafe-directory-impl</include>
                                    <include>de.adorsys:datasafe-encryption-impl</include>
                                    <include>de.adorsys:datasafe-inbox-impl</include>
                                    <include>de.adorsys:datasafe-privatestore-impl</include>
                                    <include>de.adorsys:datasafe-metainfo-version-api</include>
                                    <include>de.adorsys:datasafe-metainfo-version-impl</include>
                                    <include>de.adorsys:datasafe-storage-impl-s3</include>
                                    <include>de.adorsys:datasafe-storage-impl-fs</include>
                                </includes>
                            </artifactSet>

                            <relocations>
                                <relocation>
                                    <pattern>de.adorsys.datasafe</pattern>
                                    <shadedPattern>de.adorsys.datasafe_0_7_1</shadedPattern>
                                </relocation>
                            </relocations>

                        </configuration>
                    </execution>
                </executions>
            </plugin>
```   
</details>

But though this is done, the shaded classes are not found in the test classes of this subproject.

```
 import de.adorsys.datasafe_0_7_1.simple.adapter.api.types.DFSCredentials;
 import de.adorsys.datasafe_0_7_1.simple.adapter.impl.SimpleDatasafeServiceImpl;
```

