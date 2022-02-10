package Installation_Test;

import java.io.File;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.sql.*;
import java.util.Scanner;

public class Installation_Test 
{
	public static void main(String[] args) throws Exception
	{
	    
	}

    public static void XML_to_SQL_to_Excel(){}// I'm not sure we need this one. 

    public static void Write_to_Excel_from_SQL()
    {
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
            while (rset.next()) {
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

    public static void Write_to_Excel_from_Java() 
    {
    
        XSSFWorkbook workbook = new XSSFWorkbook(); // workbook object
        XSSFSheet spreadsheet = workbook.createSheet("Research Data"); // spreadsheet object
        XSSFRow row; // creating a row object
      
        Map<String, Object[]> ReschData = new TreeMap<String, Object[]>(); // This data is what needs to be written (Object[]) v
      
        ReschData.put("1", new Object[] { "IC_Name", "ORG_Name", "Project_Title" });
        ReschData.put("2", new Object[] { "EUNICE KENNEDY SHRIVER NATIONAL INSTITUTE OF CHILD HEALTH & HUMAN DEVELOPMENT", 
          "RESEARCH INST NATIONWIDE CHILDREN'S HOSP", "Longitudinal Assessment of Driving After Mild TBI in Teens" });
        ReschData.put("3", new Object[] { "Dr. Aubert", "AU Compuster and Cyber Science", "Teacher & Researcher" });
        ReschData.put("4", new Object[] { "Dr. Balas", "AU Allied Health", "Teacher & Researcher" });
        ReschData.put("5", new Object[] { "Sleeper", "Best Redhead in AU", "Student" });
      
        Set<String> keyid = ReschData.keySet();
        int rowid = 0;
      
        for (String key : keyid) // writing the data into the sheets
        {
          row = spreadsheet.createRow(rowid++);
          Object[] objectArr = ReschData.get(key);
          int cellid = 0;
          for (Object obj : objectArr) 
          {
            Cell cell = row.createCell(cellid++);
            cell.setCellValue((String)obj);
          }
        }
      
        // writing the workbook into the file
        FileOutputStream out = new FileOutputStream( new File("./GFGsheet.xlsx")); //C:\Users\sleep\Desktop\Excel
        workbook.write(out);
        out.close();
    }

    public static void Connect_to_DB() 
    {
        try (Connection conn = 
                DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB"
                + "?user=DBTestUser"
                + "&password=wali0e^23"
                + "&allowMultiQueries=true"
                + "&createDatabaseIfNotExist=true"
                + "&useSSL=true"); 
                Statement stmt = conn.createStatement(); ) 
        {
            Scanner myObj = new Scanner(System.in);  // Create a Scanner object
            String strSelect, Name, Website;
            int rowCount = 0;
            
            System.out.print("\nTest, Test\n");
    
            strSelect = "SELECT Name, Website FROM CompanyDB";
            PreparedStatement VACCINE_NetM = conn.prepareStatement(strSelect);
            ResultSet rset = VACCINE_NetM.executeQuery();  
    
            while (rset.next()) 
            {
            Name = rset.getString("Name");
            Website = rset.getString("Website");
            System.out.println("The " + Name + " Database, found at " + Website + ".");
            rowCount++;
            }
    
            System.out.print("\nBennie and the Jets\n");
            conn.close();
        } 
        catch (SQLException ex) 
        {
            ex.printStackTrace();
        }
        
    }

    public static void Company_DB_To_Excel()
    {
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
}


