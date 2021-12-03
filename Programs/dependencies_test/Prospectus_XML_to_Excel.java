import java.io.Console;
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

public class Prospectus_XML_to_Excel {
	public static void main(String[] args) throws Exception {
		
		Scanner myObj = new Scanner(System.in);
		XSSFWorkbook workbook = new XSSFWorkbook(); // workbook object
		String location = "RePORTER_PRJ_X_FY2022_002.xml";
		String Table = "ItemA";
		Class.forName("com.mysql.jdbc.Driver");

		ParseFromXML(location, Table);
		WriteToExcel("*", Table, workbook, location);

		// writing the workbook into the file
	}

	public static void ParseFromXML(String Location, String Table) throws Exception {
		try {
			//Class.forName("com.mysql.jdbc.Driver");
			File inputFile = new File(Location);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			int Limit = 50 + 1;
			
			String[] TagList = {"APPLICATION_ID", "ORG_CITY", "PI_NAME"};
			String[][] data = new String[Limit][TagList.length];
		
			for (int i = 0; i < TagList.length; i++) 
			{
				data[0][i] = TagList[i];
			}

			NodeList nList = doc.getElementsByTagName("row");
			System.out.println("Start Parsing from XML");
			for (int TagName = 0; TagName < TagList.length; TagName++) 
			{
				System.out.println(TagList[TagName]);
				for (int temp = 1; temp < Limit; temp++) //temp < nList.getLength()
				{
					
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) 
					{
						Element eElement = (Element) nNode;
						data[temp][TagName] = eElement.getElementsByTagName(TagList[TagName]).item(0).getTextContent();
						//System.out.println(data);
					}
					// do not enable this, you will increase your runtime greatly	
				}
			}
			WriteToSQL(Table, data, TagList, Limit);
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void WriteToSQL(String TableName, String[][] data, String[] TagName, int Limit) throws Exception {
		try (	
				Connection conn = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" + "?user=testuser" 
						+ "&password=password" + "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true" 
						+ "&useSSL=true");
				Statement stmt = conn.createStatement(); ) 
			{

			String DropTable, CreateTable, Statement;
			
			DropTable = "DROP TABLE IF EXISTS " + TableName + ";";
			System.out.println(DropTable);
			CreateTable = "CREATE TABLE " + TableName + " (" + TagName[0] + " VARCHAR(255) PRIMARY KEY, " ;

			for (int j = 0; j < TagName.length-1; j++) // creates the SWL table based on the number of strings in TagName
			{
				CreateTable = CreateTable + TagName[j+1] + " VARCHAR(255)";
				if (j != TagName.length - 2) CreateTable = CreateTable + ", ";
			}
			CreateTable = CreateTable + ")";
			System.out.println(CreateTable);
			//PreparedStatement TableDrop = conn.prepareStatement(DropTable);
			//PreparedStatement TableCreate = conn.prepareStatement(CreateTable);
			//TableDrop.execute();
			//TableCreate.execute();
			stmt.execute(DropTable);
			stmt.execute(CreateTable);

			Statement = "INSERT INTO " + TableName + " VALUES (";
			for (int j = 1; j < TagName.length; j++) // creates the Prepared Statement based on the number of strings in TagName
			{
				Statement = Statement + "?, ";
			}
			Statement = Statement + "?)";
			//System.out.println(Statement);

			PreparedStatement preparedStatement = conn.prepareStatement(Statement);

			for (int i = 0; i < Limit; i++) 
			{
				for (int j = 0; j < TagName.length; j++)
				{
					preparedStatement.setString(j+1, data[i][j]); // sets the Prepared String a number of times equal to the amount of strings in TagName
				}
				preparedStatement.execute();
			}
			conn.close();
		}
	}

	public static void WriteToExcel(String DataWanted, String Table, XSSFWorkbook workbook, String location) throws Exception 
	{
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" + 
				"?user=testuser" + "&password=password" + "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true" + "&useSSL=true");
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) 
		{

			XSSFSheet spreadsheet = workbook.createSheet("Research Data; " + location); // spreadsheet object
			XSSFRow row; // creating a row object

			System.out.println("Starting writing to Excel.");
			String strSelect = "SELECT " + DataWanted + " FROM " + Table;
			ResultSet rset = stmt.executeQuery(strSelect);

			ResultSetMetaData rsmd = rset.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			String columnValue = "";
			int rowNumber = 0;
			String ColumnName = rsmd.getColumnName(1);
			row = spreadsheet.createRow(rowNumber++);
			rset.last();

			do
			{
				for (int i = 1; i <= columnsNumber; i++) // this loop populates a cell with data
				{
					if (rsmd.getColumnName(i) == rsmd.getColumnName(1)) // this detects if the column at the current is
																		// equal to the first entry. If so, that means
																		// we need a new row
					{
						row = spreadsheet.createRow(rowNumber++); // This line create a new row
					}
					columnValue = rset.getString(i);
					//console.WriteLine(columnValue);
					row.createCell(i).setCellValue(columnValue);
				}
				FileOutputStream out = new FileOutputStream(new File("GFGsheet.xlsx")); // C:\Users\sleep\Desktop\Excel
				workbook.write(out);
			}
			while (rset.previous());
				
			conn.close();
		}
	}
}
