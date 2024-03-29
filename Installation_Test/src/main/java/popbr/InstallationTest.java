package popbr;
// mvn exec:java -Dexec.mainClass="popbr.InstallationTest"

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import java.sql.*;

import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class InstallationTest {
    public static void main(String[] args) throws Exception { 
        Class.forName("com.mysql.cj.jdbc.Driver");

        //This method returns the base filepath for the program, like C:\User\JohnS\desktop\
        //With this, the program can keep track of itself, the files it creates, and looks for necessary files 
        String BasePath = EstablishFilePath();

        //This creates a path to the example database in the downloads folder
        String TestFile = BasePath + File.separator + "target" + File.separator + "downloads" + File.separator + "test.xml";

        //This creates a path to the login information folder, then goes and retrieves login information for SQL
        String LoginPath = BasePath + File.separator + "target" + File.separator;
        String[] SQLLogin = DetermineSQLLogin(LoginPath);

        System.out.println("Attempting to connect to the Test Data file: " + Connect_to_File(TestFile));

        System.out.println("Attempting to Connect, create, and insert to an SQL database: " + Connect_to_SQL(SQLLogin));
        System.out.println("Attempting to Connect to and output from an SQL database: " + Output_from_SQL(SQLLogin));

        System.out.println("Attempting to Create and insert into an Excel: " + Connect_to_Excel(SQLLogin, BasePath));

        System.exit(0);
    }

    public static String Connect_to_File(final String Path) throws Exception {
        String result = "";
        try{
            //Sets the file to be read to the one passed, test.xml, using the path passed earlier
            File inputFile = new File(Path);
            //These prepare the XML file to be read by the program
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            //Creates a list of nodes with the XML Element name "Input" from the earlier input file
            NodeList nList = doc.getElementsByTagName("Input");

            //A list of strings is created, length of 2, as only 2 things are ever pulled: a Username and Password
            String[] MessageInfo = new String[2];

            /*
             * This goes through the list of nodes established earlier
             * and, for each node, if it matches the type we want, SQL,
             * then we pull the data from the node/message, Username and Password.
             */
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Node username = eElement.getElementsByTagName("Opener").item(0);
                    MessageInfo[0] = username.getTextContent();

                    Node password = eElement.getElementsByTagName("Closer").item(0);
                    MessageInfo[1] = password.getTextContent();
                }
            }

            //The Username and Password is concatenated
            String Message = MessageInfo[0] + " " + MessageInfo[1];

            //The username and password is tested. If it reads "Hello World", it reads "Success". 
            if (Message.equals("Hello World")) {
                result = "Success";
            } //If you get this message, then, somewhere, the message failed. 
            else result = "A file was connected to, it appears to have the wrong contents. \nCheck if any modifications have occurred to the program's target/downloads";
        }
        //This is the exception in case anything happens that forces the method to fail for reasons other than the data not matching up
        catch (Exception e) {
            result = "Failure";
            e.printStackTrace();;
        }
        return result;
    }
    
    public static String Connect_to_SQL(String[] LoginInfo) throws Exception {
        
        String result = "";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB" 
            + "?user=" + LoginInfo[0] + "&password=" + LoginInfo[1] + "&allowMultiQueries=true" 
            + "&createDatabaseIfNotExist=true" + "&useSSL=true"); 
            Statement stmt = conn.createStatement(); ) {
        
            String DropTable, CreateTable, Statement, TestDatabase, TestAttribute;

            //The following preps the creation of the database, the attributes, and the creation strings 
            TestDatabase = "TestDB";
            TestAttribute = "TestAttribute";

            //The table we need is first dropped, then created with the propper attributes. 
            //The table is  dropped to prevent SQL from running into an error where the table 
            //is already made but wasn't dropped since its creation. 
            DropTable = "DROP TABLE IF EXISTS " + TestDatabase + ";";
            CreateTable = "CREATE TABLE " + TestDatabase + " (" + TestAttribute + " VARCHAR(255) PRIMARY KEY)";
    
            stmt.execute(DropTable);
        	stmt.execute(CreateTable);

            //This sets up what will be a prepared string into the test DB.
            Statement = "INSERT INTO " + TestDatabase + " VALUES (?)";

            PreparedStatement preparedStatement = conn.prepareStatement(Statement);

            //This fills the previos PS with the data "Hello SQL"
            preparedStatement.setString(1, "Hello SQL");
            preparedStatement.execute();
         
            conn.close();

            result = "Success";
        } 
            catch (SQLException ex) 
        { 
        //If any errors happen, this is return. There's no need for a failure condition in the earlier part
        //as any error that happens would result in SQL getting the error, thus returning this. 
            result = "Failure";
            ex.printStackTrace();
        }

        return result;
    }

    public static String Output_from_SQL(String[] LoginInfo) throws Exception {

        String result;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB" 
            + "?user=" + LoginInfo[0] + "&password=" + LoginInfo[1] + "&allowMultiQueries=true" 
            + "&createDatabaseIfNotExist=true" + "&useSSL=true");
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ) {

            //This gets sets up the request from the testDB for the Test attribute
            String TestDatabase, TestAttribute;
            TestDatabase = "TestDB";
            TestAttribute = "TestAttribute";

            //This requests the the test attribute from the testDB, which returns everything in it
            String strSelect = "SELECT " + TestAttribute + " FROM " + TestDatabase;
            ResultSet rset = stmt.executeQuery(strSelect);
            rset.first();
            //This sets the pointer to read from the first entry in the result set

            //The result set's first entry is looked at. If it's not null, then the retrieval was sucessful.
            if (rset.getString(1).equals("Hello SQL")) {
                result = "Success";
            } 
            else result = "An SQL Database was connected to, but it appears to have the wrong contents.";
            //This is here in case the earler attempt to put items in the database put something that wasn't "Hello SQL"
        } catch (SQLException ex) { 
            //In the case of there being nothing to retrieve, or no database named "Testdb", 
            //or some other error, then this is returned
            result = "Failure";
            ex.printStackTrace();
        }

        return result;
    }
    
    public static String Connect_to_Excel(String[] LoginInfo, String Path) throws Exception {
        
        String result = "failure";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB" 
        + "?user=" + LoginInfo[0] + "&password=" + LoginInfo[1] + "&allowMultiQueries=true" 
        + "&createDatabaseIfNotExist=true" + "&useSSL=true"); 
        Statement stmt = conn.createStatement();)	  
        { 
            //The following 
            Scanner myObj = new Scanner(System.in);
            XSSFWorkbook workbook = new XSSFWorkbook(); // workbook object
            XSSFSheet spreadsheet = workbook.createSheet("Test Sheet"); // spreadsheet object
            XSSFRow row; // creating a row object

            //This queries all the info in the TestDB Database and returns it as a resultset
            String strSelect = "SELECT * FROM TestDB";
            ResultSet rset = stmt.executeQuery(strSelect);

            //This gets the length and width of the database result set to know how long to go for in the loop below.
            ResultSetMetaData rsmd = rset.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            String columnValue = "";
            int rowNumber = 0;
            String ColumnName = rsmd.getColumnName(1);
            row = spreadsheet.createRow(rowNumber++);

            //This goes through each entry in the resultset and puts it, entry by entry, into the Excel sheet.
            while (rset.next()) 
            {
                for (int i = 1; i <= columnsNumber; i++) // this loop populates a cell with data
                {
                    if (rsmd.getColumnName(i) == rsmd.getColumnName(1)) //this detects if the column at the current is equal to the first entry. If so, that means we need a new row
                    {
                        row = spreadsheet.createRow(rowNumber++); // This line create a new row
                    }
                    columnValue = rset.getString(i);
                    row.createCell(i).setCellValue(columnValue);
                } 
            } 
            // This writes the workbook into an excel, with the filepath listed below. The workbook is then quit out of and closed.
            FileOutputStream out = new FileOutputStream(new File(Path + File.separator + "target" + File.separator + "GFGsheet.xlsx")); //C:\Users\sleep\Desktop\Excel
            workbook.write(out);
            out.close();
            result = "Success";
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return result;
    }

    public static String EstablishFilePath() throws Exception {
        try {

            //This creates a dummy file that starts as the basis for creating the filepath in the base of the program
            File s = new File("f.txt");
            String FilePath = "";
            
            //This gets the filepath of the dummy file and transforms it into characters, so it can be modified.
            //The modification snips off the charcters "f.txt" so that the only path left is the base filepath
            char[] tempChar = s.getAbsolutePath().toCharArray();
            char[] newChar = new char[tempChar.length - 6];
            for (int i = 0; i < newChar.length; i++) {
                newChar[i] = tempChar[i];
            }
            //This makes the filepath into a string, minus the "f.txt" bit
            FilePath = String.valueOf(newChar);
            //System.out.println(FilePath);
            //This returns the filepath
            return FilePath;

	    } catch (Exception e) {
            e.printStackTrace();;
            return "failed to find filepath";
        }
    }

    public static String[] GetLoginInfo(String Location, String[] Elements) throws Exception{	
        
        String[] LoginInfo = new String[2];
        try {

            //Sets the file to be read to the one passed, test.xml, using the path passed earlier
            File inputFile = new File(Location);
            //These prepare the XML file to be read by the program
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            //This gets the elements from the document that are Login and makes it into a list of nodes
            NodeList nList = doc.getElementsByTagName("Login");

            //This prepares the Login info to be received 
            
            /*This goes through the list of nodes and matches it to nodes that are have login info for SQL
            * When it finds the SQL node, it logs information it has, the Username and Password, it puts it
            * into the prepared string list.
            */
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Node username = eElement.getElementsByTagName("Username").item(0);
                    LoginInfo[0] = username.getTextContent();

                    Node password = eElement.getElementsByTagName("Password").item(0);
                    LoginInfo[1] = password.getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // The Username and password is passed on
        return LoginInfo;
    }
    public static String[] DetermineSQLLogin(String Location) throws Exception {

		String[] result = {"", ""};
		String[] SQLElementList = { "Login", "Username", "Password" };
		File LoginInfo = new File(Location + "LoginInfo.xml");

		if(LoginInfo.isFile()) {
			result = GetLoginInfo(Location + "LoginInfo.xml", SQLElementList);
		} else {
			result = GetLoginInfo(Location + "LoginInfoTemplate.xml", SQLElementList);
		}
		return result;
	}
}
