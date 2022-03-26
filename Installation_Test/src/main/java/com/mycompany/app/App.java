package com.mycompany.app;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import java.sql.*;

import java.text.CharacterIterator;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class App {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String BasePath = EstablishFilePath();

		String TestFile = BasePath + File.separator + "target" + File.separator + "downloads" + File.separator + "test.xml";

        String LoginPath = BasePath + File.separator + "target" + File.separator + "LoginInfo.xml";
		String[] SQLLogin = GetLoginInfo("SQL", LoginPath);

        System.out.println("Attempting to connect to the Test Data file: " + Connect_to_File(TestFile));

        System.out.println("Attempting to Connect, create, and insert to an SQL database: " + Connect_to_SQL(SQLLogin));
        System.out.println("Attempting to Connect to and output from an SQL database: " + Output_from_SQL(SQLLogin));

        System.out.println("Attempting to Create and insert into an Excel: " + Connect_to_Excel());

        //System.out.println( "hey" );
        System.exit(0);
    }

    public static String Connect_to_File(String Path) throws Exception {

        String result = "";
        try{
            File inputFile = new File(Path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Input");

            String[] MessageInfo = new String[2];

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

            String Message = MessageInfo[0] + " " + MessageInfo[1];

            if (Message.equals("Hello World")) {
                result = "Success";
            } 
            else result = "A file was connected to, it appears to have the wrong contents. \nCheck if any modifictions have occured to the program's target/downloads";
        }

        catch(Exception e) {
            result = "Failure";
            e.printStackTrace();;
        }
        return result;
    }
    
    public static String Connect_to_SQL(String[] LoginInfo) throws Exception {
        
        String result = "";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" 
            + "?user=" + LoginInfo[0] + "&password=" + LoginInfo[1] + "&allowMultiQueries=true" 
            + "&createDatabaseIfNotExist=true" + "&useSSL=true"); 
            Statement stmt = conn.createStatement(); ) {

            String DropTable, CreateTable, Statement, TestDatabase, TestAttribute;

            TestDatabase = "TestDB";
            TestAttribute = "TestAttribute";

            DropTable = "DROP TABLE IF EXISTS " + TestDatabase + ";";
            CreateTable = "CREATE TABLE " + TestDatabase + " (" + TestAttribute + " VARCHAR(255) PRIMARY KEY)";
    
            stmt.execute(DropTable);
			stmt.execute(CreateTable);

            Statement = "INSERT INTO " + TestDatabase + " VALUES (?)";

            PreparedStatement preparedStatement = conn.prepareStatement(Statement);

            preparedStatement.setString(1, "Hello SQL");
            preparedStatement.execute();
         
            conn.close();

            result = "Success";
        } 
            catch (SQLException ex) 
        {
            result = "Failure";
            ex.printStackTrace();
        }

        return result;
    }

    public static String Output_from_SQL(String[] LoginInfo) throws Exception {
        
        String result;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" 
            + "?user=" + LoginInfo[0] + "&password=" + LoginInfo[1] + "&allowMultiQueries=true" 
            + "&createDatabaseIfNotExist=true" + "&useSSL=true");
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ) {

            String TestDatabase, TestAttribute;
            TestDatabase = "TestDB";
            TestAttribute = "TestAttribute";

            String strSelect = "SELECT " + TestAttribute + " FROM " + TestDatabase;
            ResultSet rset = stmt.executeQuery(strSelect);
            rset.first();

            if (rset.getString(1).equals("Hello SQL")) {
                result = "Success";
            } 
            else result = "An SQL Database was connected to, it appears to have the wrong contents.";
        } 
            catch (SQLException ex) 
        {
            result = "Failure";
            ex.printStackTrace();
        }

        return result;
    }
    
    public static String Connect_to_Excel() throws Exception {

        String result = "";
        try{

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("Data");
            XSSFRow row; // creating a row object

            row = spreadsheet.createRow(0);
            row.createCell(0).setCellValue("Hola Mundo");

            FileOutputStream out = new FileOutputStream(new File("GFGsheet.xlsx")); // C:\Users\sleep\Desktop\Excel
            workbook.write(out);

            result = "Success";
        }
        catch(Exception e) {
            result = "Failure";
            e.printStackTrace();;
        }
        return result;
    }
    
    public static void Conhgvnect_to_Excel() throws Exception {
        try (Connection conn = 
        DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB"
        + "?user=DBTestUser"
        + "&password=wali0e^23"
        + "&allowMultiQueries=true"
        + "&createDatabaseIfNotExist=true"
        + "&useSSL=true"); 
        Statement stmt = conn.createStatement();)	  
        { 
            Scanner myObj = new Scanner(System.in);
            XSSFWorkbook workbook = new XSSFWorkbook(); // workbook object
            XSSFSheet spreadsheet = workbook.createSheet("Research Data"); // spreadsheet object
            XSSFRow row; // creating a row object

            String strSelect = "SELECT * FROM CompanyDB";
            ResultSet rset = stmt.executeQuery(strSelect);

            ResultSetMetaData rsmd = rset.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            String columnValue = "";
            int rowNumber = 0;
            String ColumnName = rsmd.getColumnName(1);
            row = spreadsheet.createRow(rowNumber++);
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
            // writing the workbook into the file
            FileOutputStream out = new FileOutputStream( new File("C:\\Users\\sleep\\Desktop\\Excel\\GFGsheet.xlsx")); //C:\Users\sleep\Desktop\Excel
            workbook.write(out);
            out.close();
        }
    }

    public static String EstablishFilePath() throws Exception {
		File s = new File("f.txt");
		String FilePath = "";
		char[] tempChar = s.getAbsolutePath().toCharArray();
		char[] newChar = new char[tempChar.length - 6];
		for (int i = 0; i < newChar.length; i++) {
			newChar[i] = tempChar[i];
		}
		FilePath = String.valueOf(newChar);
        //System.out.println(FilePath);
		return FilePath;
	}

    public static String[] GetLoginInfo(String LoginType, String Location) throws Exception{
		
		File inputFile = new File(Location);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();
		
		NodeList nList = doc.getElementsByTagName("Login");

		String[] LoginInfo = new String[2];

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

		return LoginInfo;
    }

}
