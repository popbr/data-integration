ReadMe

Should be documented:
How to compile,
How to execute,
Which parameter(s) can be tweaked (typically, the user / password of the database user),
How to use the project.

>The above is just a reference as I type this out. It will be removed when the ReadMe is satisfactory.
# Popbr Data-integration ReadMe 
[Github](https://github.com/popbr/data-integration)
## Compilation and Execution
### Compilation
To compile the program, open an instance of Terminal and navigate to the directory where the program is installed. From here, change the directory again to the application, which is found at 
***data-integration/Project/Database-IO/***. Use the command: `mvn compile`. Once compiled, you can now execute it.

### Execution
To execute the program, go the the application's location in the directory and run the following command: `mvn exec:java -Dexec.mainClass="com.mycompany.app.App"`.

After Compiling and Executing, the program should be running. If you run into any errors/either step fails, try the following:
1. Run the installation test and see if the same error persists.
2. Make sure you're executing the commands in the correct directory.
    1. for example: `cd C:\Users\JDoe\Desktop\data-integration\Project\Database-IO` will set the current direction to the program's folder
3. Try uninstalling and reinstalling the program
4. Open an issue on our [Github](https://github.com/popbr/data-integration)
    1. Note: Is that something that should be suggested? should it be one of our emails instead?

## Changing Parameters
Please ensure that you change the following parameters to suit your use of this program. 

### Login Information
Login Information for SQL, found in the *data-integration/Project/Database-IO/target/LoginInfo.xml* file under the “SQL” heading, can be changed by editing the appropriate tag contents for Username or Password.

### Websites Scanned
Websites you wish to be scanned, found in the *data-integration/Project/Database-IO/target/DBInfo.xml* file, can be edited by changing "Name" and "URL" tags. Please note you must create a new DB id to have the new webpage properly scanned.

### Databases Scanned
Database files you want scanned without going to a website, which you can put in the downloads folder at *.../Database-IO/target/Downloads/*. Dropping the database file in this folder is all that has to be done. 

## Use of the Program
To use the program, at least one database must be put in the downloads folder at *.../Database-IO/target/Downloads/*. Alternatively, you can specify websites to scrape for database, or allow pre-selected databases to be scrapped. If you wish to look for only a certain entity (school, person, research group, etc.), then that can be specified at runtime (TO BE IMPLEMENTED). 

Once the program has compiled the databases, it will then scrape them for entities and attributes toput into SQL. These pieces ofinformation will then be matched, and then the researching entities can be searched for. Finally, That information will be returned as an Excel Sheet, created in the  *data-integration/Project/Database-IO/* folder. 

## Terms of Use
This project may be downloaded for the purposes of academic research or inquery, but please note that this project is only a tool for the collection and examination of databases and the data within said databases. This project and its authors do not claim ownership over any files downloaded or any data handled by the project, nor do they claim 100% accuracy in conclusions drawn from any results user may recieve.

