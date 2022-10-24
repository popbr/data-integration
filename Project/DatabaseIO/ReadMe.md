<!--
ReadMe
Should be documented:
How to compile,
How to execute,
Which parameter(s) can be tweaked (typically, the user / password of the database user),
How to use the project.

>The above is just a reference as I type this out. It will be removed when the ReadMe is satisfactory.
-->


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
Login Information for SQL can be found here:
>*data-integration/Project/Database-IO/target/LoginInfo.xml*

 To change a specific username and password, like SQL, look for that entity's heading and edit the tag contents for Username or Password.

### Websites Scanned
Websites you wish to be scanned can be added/found in:
> *data-integration/Project/Database-IO/target/DBInfo.xml*

Removing websites requires the deletion of the entire website's id, including name and URL.
Adding websites requires a new DB id to have the new webpage properly scanned, with the name and URL to the database file.

### Databases Scanned
Database files you want scanned that you already have downloaded can be put in the downloads folder at: 
> *.../Database-IO/target/Downloads/*. 

Dropping the database file in this folder is all that has to be done. 
The types of files that are accepted are in the .xml, .xlsx, .txt, or .csv format.

#### Example:
With an example file, *DBExample.xml*, you would have move this file in to the Downloads section of the Database-IO folder, and leave it there. Nothing further has to be done.  

# Usage

## Cloning and setting up the enviornment
1. Clone the repository.
    > git clone https://github.com/popbr/data-integration.git  
2. Run the Installation Test to make sure your use of DatabaseIO will work
    1.  To execute the Installation Test, go the the application's location at
        > C:\Users\sleep\Desktop\data-integration\Installation_Test
   
    2.  run the following command: `mvn exec:java -Dexec.mainClass="com.mycompany.app.App"`.
3. Make sure to have the following items on your setup
- MySQL Server 8.0
- mySQL Workbench 8.0
- Java technologies:
    - JDK 16
    - JRE 1.8 or higher
- Maven
4. It is strongly suggested you have a Spreadsheet software
    - this Program uses Excel as its default, but other variants, like LibreOffice, work as well   


## Using the Program
Before running the program, make sure:
> You have at least one database in the downloads folder at *.../Database-IO/target/Downloads/*. 
> These databases are files that catalogue researching entities with attributesor classify/detail than.
> These files can be in the format of .xml, .xlsx, .txt, or .csv
> For more instructions on this, see the "Databases Scanned" section

or
> You can specify websites to scrape for database/allow pre-selected databases to be scrapped. 
> For instructions on this, see the "Websites Scanned" Section

If you wish to look for only a certain entity (school, person, research group, etc.), then that can be specified at/after runtime (TO BE IMPLEMENTED). 

Resulting information from searches will be returned as an Excel Sheet, created in the  *data-integration/Project/Database-IO/* folder. 

## Terms of Use
This project may be downloaded for the purposes of academic research or inquery, but please note that this project is only a tool for the collection and examination of databases and the data within said databases. This project and its authors do not claim ownership over any files downloaded or any data handled by the project, nor do they claim 100% accuracy in conclusions drawn from any results user may recieve.