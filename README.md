# SwornAPI
SwornAPI is a collection of useful classes that I use in most of my projects. It contains APIs for configuration, commands, chat, GUIs, reflection, integration, I/O, and more. 

### Resources
* [Download](https://ci.dmulloy2.net/job/SwornAPI)
* [JavaDocs](https://ci.dmulloy2.net/job/SwornAPI/javadoc)

### Building
SwornAPI is built using Maven. It requires Spigot and the Spigot API, which can be obtained [here](https://www.spigotmc.org/wiki/buildtools/).

### License
SwornAPI is currently licensed under the GPL v3.

### Utilizing SwornAPI
SwornAPI is best utilized as a Maven dependency:

````xml
<repositories>
  <repository>
    <id>jitpack</id>
    <url>https://jitpack.io</url>
  </repository>
  <!-- And so on -->
</repository>

<dependencies>
  <dependency>
    <groupId>com.github.dmulloy2</groupId>
    <artifactId>SwornAPI</artifactId>
    <version>-SNAPSHOT</version>
  </dependency>
  <!-- And so on -->
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