package Prospectus_Write_to_Excel;

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

public class Prospectus_Write_to_Excel 
{
	  public static void main(String[] args) throws Exception
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
}


