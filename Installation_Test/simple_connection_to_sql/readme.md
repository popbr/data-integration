The Prospectus_Connect_To_DB.java program assumes
- That you have

    - MySQL Server 8.0
    - mysql-connector-java-8.0.23

    available,
    
- That the credential to your database, hosted at localhost:3306/Prospectus_DB, are
    - Username: DBTestUser
    - Password: wali0e^23

Normally, the makefile contains rules to download the connector (`make mysql-connector-java-8.0.26.jar`), compile the .java file (`make Prospectus_Connect_To_DB.class`), create the database (`make db`) and finaly execute the java program (`make all`).
