# Testing Your Installation

This programs serves to test if your set-up match our requirements.

<!--
To work properly, you should be running a MySQL Server (8.0) with a user "DBTestUser" whose password is "wali0e^23". You can edit those information 
-->

See POM file for dependencies, you need to be running a be running a MySQL Server (8.0).

For compiling and running the program, please use...

```
cd Installation_Test
mvn compile
mvn exec:java -Dexec.mainClass="popbr.InstallationTest"
```

Expected output after executing:

```text
[INFO] Scanning for projects...
[INFO] 
[INFO] ----------------------< popbr:Installation_Test >-----------------------
[INFO] Building Installation_Test 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- exec-maven-plugin:3.0.0:java (default-cli) @ Installation_Test ---
Attempting to connect to the Test Data file: Success
Attempting to Connect, create, and insert to an SQL database: Success
Attempting to Connect to and output from an SQL database: Success
Attempting to Create and insert into an Excel: Success
```

### Login Information

Login Information for SQL can be found here:
>*data-integration/Project/Database-IO/target/LoginInfoTemplate.xml*

To change a specific username and password, like SQL, you must create a new file, titled *LoginInfo.xml* in the *data-integration/Installation_Test/target/* folder. To do this efficiently, copy and rename the *LoginInfoTemplate.xml* file. Then edit the username and password fields to match the required information.