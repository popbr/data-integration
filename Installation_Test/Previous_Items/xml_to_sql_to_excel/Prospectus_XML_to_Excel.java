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
		ParseFromXML(location);

		WriteToExcel("*", workbook, 0);

		// writing the workbook into the file
	}

	public static void ParseFromXML(String Location) throws Exception {
		try {
			File inputFile = new File(Location);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("row");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					; // get the data from the node ie: the Name tag name would give "Aubert" or
						// "Sleeper"
					WriteToSQL("row", eElement.getElementsByTagName("APPLICATION_ID").item(0).getTextContent(), temp);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// insert2 = "INSERT INTO VACCINE VALUES ( ?, ?)";
	// TableDrop = "DROP TABLE IF EXISTS DISTRIBUTED;";
	// TableCreation = "CREATE TABLE DISTRIBUTED ("+ "Administered INT NOT NULL, "
	// +"Distributor VARCHAR(50) PRIMARY KEY, " +
	// "FOREIGN KEY (Distributor) REFERENCES COMPANY (NAME) ON UPDATE CASCADE );";
	public static void WriteToSQL(String TableName, String data, int NewTable) throws Exception {
		try (Connection conn = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" + "?user=testuser" + "&password=password"
						+ "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true" + "&useSSL=true");
				Statement stmt = conn.createStatement();) {
			String DropTable = "";
			String CreateTable = "";
			if (NewTable == 0) {
				DropTable = "DROP TABLE IF EXISTS " + TableName + ";";
				CreateTable = "CREATE TABLE " + TableName + " (" + TableName + " VARCHAR(50) PRIMARY KEY);";
				PreparedStatement TableDrop = conn.prepareStatement(DropTable);
				PreparedStatement TableCreate = conn.prepareStatement(CreateTable);
				TableDrop.execute();
				TableCreate.execute();
			}
			String DataInsert = "INSERT INTO " + TableName + " VALUE ('" + data + "');";
			PreparedStatement InsertData = conn.prepareStatement(DataInsert);
			InsertData.executeUpdate();
			conn.close();
		}
	}

	// /PreparedStatement DropTable = conn.prepareStatement(TableDrop);
	// PreparedStatement NewTable = conn.prepareStatement(TableCreation);
	// DropTable.execute();
	// NewTable.execute();
	public static void WriteToExcel(String Table, XSSFWorkbook workbook, int SpreadsheetNum) throws Exception {
		try (Connection conn = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" + "?user=testuser" + "&password=password"
						+ "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true" + "&useSSL=true");
				Statement stmt = conn.createStatement();) {

			XSSFSheet spreadsheet = workbook.createSheet("Research Data " + SpreadsheetNum); // spreadsheet object
			XSSFRow row; // creating a row object

			String strSelect = "SELECT " + Table + " FROM CompanyDB";
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
					if (rsmd.getColumnName(i) == rsmd.getColumnName(1)) // this detects if the column at the current is
																		// equal to the first entry. If so, that means
																		// we need a new row
					{
						row = spreadsheet.createRow(rowNumber++); // This line create a new row
					}
					columnValue = rset.getString(i);
					row.createCell(i).setCellValue(columnValue);
				}
				FileOutputStream out = new FileOutputStream(new File("GFGsheet.xlsx")); // C:\Users\sleep\Desktop\Excel
				workbook.write(out);
				// out.close();
			}
			conn.close();
		}
	}
}
