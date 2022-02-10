mysql-connector-java-8.0.26.jar:
	wget https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-8.0.26.tar.gz
	tar -xvzf mysql-connector-java-8.0.26.tar.gz
	mv mysql-connector-java-8.0.26/mysql-connector-java-8.0.26.jar .
	rm -rf mysql-connector-java-8.0.26 mysql-connector-java-8.0.26.tar.gz

Prospectus_Connect_To_DB.class: Prospectus_Connect_To_DB.java
	javac Prospectus_Connect_To_DB.java

db: Prospectus_DB.sql
	mysql -u testuser -ppassword < Prospectus_DB.sql

all: db Prospectus_Connect_To_DB.class mysql-connector-java-8.0.26.jar
	java -cp .:mysql-connector-java-8.0.26.jar Prospectus_Connect_To_DB
