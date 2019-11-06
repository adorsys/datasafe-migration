This is tool for an offline migration of datasafe.

To compile the code, the old datasafe has to be compiled with
maven shade plugin. For that, datasafe has to be checked out:


```
git checkout -b release-6.1 a2549140544925e861d7d895a7e72cec9a5ab7a3
```

This switched code back to relaase 6.1.
Then the ```datasafe-simple-adapter/datasafe-simple-adapter-impl.pom.xml```
has to get a final ```<plugin> ``` in the ```<build>``` section.

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
                            <relocations>
                                <relocation>
                                    <pattern>de.adorsys.datasafe</pattern>
                                    <shadedPattern>de.adorsys.datasafe_0_6_1</shadedPattern>
                                </relocation>
                            </relocations>

                            <artifactSet>
                                <excludes>
                                    <exclude>com.*</exclude>
                                    <exclude>org*</exclude>
                                    <exclude>lombok.*</exclude>
                                    <exclude>mozilla.*</exclude>
                                    <exclude>javax.inject*</exclude>
                                    <exclude>software.*</exclude>
                                </excludes>
                            </artifactSet>

                            <createDependencyReducedPom>true</createDependencyReducedPom>
                            <shadedArtifactAttached>false</shadedArtifactAttached>
                            <minimizeJar>true</minimizeJar>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```

For the current / latest release the same has to be done with
the ```datasafe-simple-adapter/datasafe-simple-adapter-impl.pom.xml```

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
                            <relocations>
                                <relocation>
                                    <pattern>de.adorsys.datasafe</pattern>
                                    <shadedPattern>de.adorsys.datasafe_0_7_0</shadedPattern>
                                </relocation>
                            </relocations>

                            <artifactSet>
                                <excludes>
                                    <exclude>com.*</exclude>
                                    <exclude>org*</exclude>
                                    <exclude>lombok.*</exclude>
                                    <exclude>mozilla.*</exclude>
                                    <exclude>javax.inject*</exclude>
                                    <exclude>software.*</exclude>
                                </excludes>
                            </artifactSet>

                            <createDependencyReducedPom>true</createDependencyReducedPom>
                            <shadedArtifactAttached>true</shadedArtifactAttached>
                            <shadedClassifierName>datasafe-version-0-7-1</shadedClassifierName>
                            <minimizeJar>true</minimizeJar>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

```

When the two shade-jar have been built, this project becomes compilable.

