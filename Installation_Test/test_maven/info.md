# On Dr. Aubert's computer

```
$mvn -version
Apache Maven 3.6.0
Maven home: /usr/share/maven
Java version: 11.0.14, vendor: Debian, runtime: /usr/lib/jvm/java-11-openjdk-amd64
Default locale: fr_FR, platform encoding: UTF-8
OS name: "linux", version: "4.19.0-18-amd64", arch: "amd64", family: "unix"
```

Following <https://maven.apache.org/guides/getting-started/#how-do-i-make-my-first-maven-project>, 

```
mvn -B archetype:generate -DgroupId=com.mycompany.app -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4
```


Following <https://stackoverflow.com/a/8202181>, the pom.xml was edited to add the dependencies to Apache's Poi.

I then edited `src/main/java/com/mycompany/app/App.java` to copy-and-paste your code from `Write_to_Excel_from_java/Prospectus_Write_to_Excel`, simply changing the class name and leaving the package declaration on the very first line.

Then,

```
mvn compile
mvn  exec:java -Dexec.mainClass="com.mycompany.app.App
```


From there, _a lot_ remains to be done: reading <https://maven.apache.org/guides/getting-started/> and <https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html> should help in getting started. Obviously, better company, package, url, etc, will need to be declared, but that's a start.

NOTE TO/FROM NOAH:
Everything was compiled and run without difficulty. Now, put Maven on the Installation Test and Dependencies Test, after configuring Excel to one sheet.