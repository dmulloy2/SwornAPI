# SwornAPI
A common API for MineSworn and Shadowvolt plugins.

### Resources
* [Download](http://ci.dmulloy2.net/job/SwornAPI)
* [JavaDocs](http://ci.dmulloy2.net/job/SwornAPI/javadoc)

### Building
SwornAPI is built using Maven. It requires Spigot and the Spigot API, which can be obtained [here](https://www.spigotmc.org/wiki/buildtools/).

### License
SwornAPI is currently licensed under the GPL v3.

### Utilizing SwornAPI
SwornAPI is best utilized as a Maven dependency like so:
````xml
<repositories>
  <repository>
    <id>dmulloy2-repo</id>
    <url>http://repo.dmulloy2.net/content/groups/public/</url>
  </repository>
  ...
</repository>

<dependencies>
  <dependency>
    <groupId>net.dmulloy2</groupId>
    <artifactId>swornapi</artifactId>
    <version>1.1-SNAPSHOT</version>
  </dependency>
</dependencies>
````

For the best results, SwornAPI should be shaded into your project:

````xml
<build>
  <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>2.3</version>
        <executions>
          <execution>
            <phase>package</phase>
            <goals>
              <goal>shade</goal>
            </goals>
            <configuration>
              <artifactSet>
                <includes>
                  <include>net.dmulloy2:swornapi*</include>
                </includes>
              </artifactSet>
              <relocations>
                <relocation>
                  <pattern>net.dmulloy2</pattern>
                  <shadedPattern>net.dmulloy2.ultimatearena</shadedPattern>
                  <excludes>
                    <exclude>net.dmulloy2.ultimatearena*</exclude>
                  </excludes>
                </relocation>
              </relocations>
            </configuration>
          </execution>
        </executions>
      </plugin>
      <!-- And so on -->
    </plugins>
</build>
````