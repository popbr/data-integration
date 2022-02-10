import java.io.Console;
import java.io.File;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xssf.usermodel.XSSFCell; //Newest addition
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.io.File;

public class Prospectus_XML_to_Excel {
	public static void main(String[] args) throws Exception {

		System.out.println("");
		Scanner myObj = new Scanner(System.in);
		XSSFWorkbook workbook = new XSSFWorkbook(); // workbook object

		File s = new File("f.txt");
		System.out.println(s.getAbsolutePath());
		String FilePath = "";
		char[] tempChar = s.getAbsolutePath().toCharArray();
		char[] newChar = new char[tempChar.length - 6];
		for (int i = 0; i < newChar.length; i++) {
			newChar[i] = tempChar[i];
		}
		
		FilePath = String.valueOf(newChar) + "\\Downloads\\";
		//System.out.println(FilePath);
		File f = new File(FilePath);
		
		File[] fileNames = f.listFiles();

		String fType, Table;
		Class.forName("com.mysql.jdbc.Driver");
		String[] TagList = { "APPLICATION_ID", "ORG_CITY", "ORG_NAME", "PI_NAMEs" };
		String[] tsvTagList = { "Title", "Status", "Locations" };
		String[] Search = {"Medical University of South Carolina", "ORG_NAME"}; // plug this into all parsing methods
		for (int i = 0; i<fileNames.length; i++) {
			System.out.println("File path Is:"+fileNames[i].getPath()+".");
			fType = FilenameUtils.getExtension(fileNames[i].getName());
			Table = fType + (i + 1);
			if (fType.equals("xml"))
				ParseFromXML(fileNames[i].getPath(), Table, TagList);
			else if (fType.equals("csv"))
				ParseFromtxt(fileNames[i].getPath(), Table, "\",\"", TagList, Search);
			else if (fType.equals("tsv"))
				ParseFromtxt(fileNames[i].getPath(), Table, "	", tsvTagList, null);
			else if (fType.equals("txt") && fileNames[i].getName().equals("ReadMeDownloads.txt")) {
				//intentionally blank
			}
				

			if (!fileNames[i].getName().equals("ReadMeDownloads.txt")) 
			WriteToExcel("*", Table, workbook, fileNames[i].getName());
		}
		

		/*
		for (int i = 0; i < fileLocation.length; i++) {
			fType = FilenameUtils.getExtension(fileLocation[i]);
			Table = fType + (i + 1);
			if (fType.equals("xml"))
				ParseFromXML(fileLocation[i], Table, TagList);
			else if (fType.equals("csv"))
				ParseFromtxt(fileLocation[i], Table, "\",\"", TagList, Search);
			else if (fType.equals("tsv"))
				ParseFromtxt(fileLocation[i], Table, "	", tsvTagList, null);

			WriteToExcel("*", Table, workbook, fileLocation[i]);
		}*/
	}

	public static void ParseFromtxt(String txtlocation, String Table, String Delim, String[] TagList, String Search[]) throws Exception {
		Scanner txtFile = new Scanner(new File(txtlocation));

		String txtFields = txtFile.nextLine();
		Scanner txtFieldsLine = new Scanner(txtFields);
		txtFieldsLine.useDelimiter(Delim);

		int index = 0;
		int Limit = 9050 + 1;

		while (txtFile.hasNextLine()) {
			index++;
			txtFile.nextLine();
		}
		if (index < Limit)
			Limit = (index + 1);
		index = 0;
		txtFile.reset();
		txtFile = new Scanner(new File(txtlocation));

		txtFields = txtFile.nextLine();
		txtFieldsLine = new Scanner(txtFields);
		txtFieldsLine.useDelimiter(Delim);

		int indexTracker = 0;
		String currentWord = "";
		String currentLine = "";

		int[] TagIndex = new int[TagList.length];
		String[][] data = new String[Limit][TagList.length];

		for (int i = 0; i < TagList.length; i++) {
			data[0][i] = TagList[i];
		}

		while (txtFieldsLine.hasNext()) {

			currentWord = txtFieldsLine.next();

			if (index == 0) { // Makes sure the first tag will not has a " at the front
				char[] tempChar = currentWord.toCharArray();
				char[] newChar = new char[tempChar.length - 1];
				for (int i = 1; i < newChar.length + 1; i++) {
					newChar[i - 1] = tempChar[i];
				}
				currentWord = String.valueOf(newChar);
			}
			if (!(txtFieldsLine.hasNext())) { // makes sure the last tag will no have a " at the end
				char[] tempChar = currentWord.toCharArray();
				char[] newChar = new char[tempChar.length - 1];
				for (int i = 0; i < newChar.length; i++) {
					newChar[i] = tempChar[i];
				}
				currentWord = String.valueOf(newChar);
			}
			for (int p = 0; p < TagList.length; p++) {
				if (currentWord.equalsIgnoreCase(TagList[p])) {
					TagIndex[indexTracker] = index;
					indexTracker++;
				}
			}
			index++;
		}
		indexTracker = 0;
		//System.out.print(indexTracker + ": ");
		//for (int i = 0; i < TagList.length -1; i++) {
		//	System.out.print(data[indexTracker][i]+ ", ");
		//}
		//System.out.println(data[indexTracker][TagList.length-1]);
		//data[indexTracker][0]+ ", " data[indexTracker][1]+ ", " + data[indexTracker][2]);
		indexTracker = 1;
			
		do {
			index = 0;
			currentLine = txtFile.nextLine();
			txtFieldsLine = new Scanner(currentLine);
			txtFieldsLine.useDelimiter(Delim);
			char quote = '"';
			while (txtFieldsLine.hasNext()) {
				currentWord = txtFieldsLine.next();
				if (index == 0) {

					char[] tempChar = currentWord.toCharArray();
					if (tempChar[0] == quote) {
						char[] newChar = new char[tempChar.length - 1];

						for (int i = 1; i < newChar.length + 1; i++) {
							newChar[i - 1] = tempChar[i];
						}
						currentWord = String.valueOf(newChar);
					}
				}
				if (index == (TagList.length - 1)) {
					char[] tempChar = currentWord.toCharArray();
					if (tempChar == null || tempChar.length == 0) { //this deals with entries such as " , ;"
						currentWord = "null";
					} 
					else {
					char[] newChar = new char[tempChar.length - 1];
					for (int i = 0; i < newChar.length; i++) {
						newChar[i] = tempChar[i];
					}
					currentWord = String.valueOf(newChar);
				} }

				for (int ind = 0; ind < TagIndex.length; ind++) {
					if (index == TagIndex[ind]) {
						data[indexTracker][ind] = currentWord;
					}
				}
				index++;
			}
			//System.out.println(indexTracker + ": " + data[indexTracker][0] + ", " +
			//data[indexTracker][1] + ", " + data[indexTracker][2] + ", " + data[indexTracker][3]);

			indexTracker++;

		} while (txtFile.hasNextLine() && indexTracker < Limit);
		// so, Noah, problem here is that some fields only have ", ;" in them. If that field happens to be looked at and edited
		// then that'sgoing to trip an alarm, becausethe field



		/*
		 * if (true)
		 * {
		 * for (int i = 0; i<Limit; i++) {
		 * System.out.print(i + ": ");
		 * for (int j = 0; j<3; j++) {
		 * System.out.print(data[i][j]+", ");
		 * }
		 * System.out.println();
		 * }
		 * }
		 */
		if (Search != null) 
		{
			int searchIndex = 0;
			for (int i = 0; i<TagIndex.length; i++) {
				if (Search[1].equalsIgnoreCase(data[0][i])) {
					searchIndex = i; 
				} 
			}

			int searchHits = 0;
			for (int i = 1; i<Limit; i++) {
				if (Search[0].equalsIgnoreCase(data[i][searchIndex])) {
					searchHits++; 
				} //Search = {"Medical University of South Carolina", 3};
			}
			//System.out.println("Number of hits is "+searchHits);

			String[][] SearchData = new String[searchHits+1][TagIndex.length];

			for (int i = 0; i<TagIndex.length; i++) {
				SearchData[0][i] = data[0][i];
			}
			int max = searchHits;
			searchHits = 0;
			for (int i = 0; i<Limit; i++) {
				if (data[i][searchIndex].equalsIgnoreCase(Search[0])) {
					for(int j = 0; j<TagIndex.length; j++) {
						SearchData[searchHits+1][j] = data[i][j];
					}
					searchHits++;
					//System.out.println("Hit found at "+ i);
					if (searchHits == max) break;
				} //Search = {"Medical University of South Carolina", 3};
			}

		 WriteToSQL(Table, SearchData, TagList, searchHits+1);
		} 
		else 
		{
			WriteToSQL(Table, data, TagList, Limit);
		}
		txtFile.close();
		txtFieldsLine.close();

	}

	public static void ParseFromExcel(String ExcelLocation, String Table, String[] TagList) throws Exception {
		try {

			// FileInputStream inputStream = new FileInputStream(new File(ExcelLocation));
			File inputFile = new File(ExcelLocation);
			XSSFWorkbook ParsingWorkbook = new XSSFWorkbook(); // po0ssible bug sol: change "ParsingWorkbook" with
																// "workbook"
			XSSFSheet ParsingSheet = ParsingWorkbook.getSheetAt(0);

			int Limit = 50 + 1;

			int[] TagIndex = new int[TagList.length];
			String[][] data = new String[Limit][TagList.length];

			for (int i = 0; i < TagList.length; i++) {
				data[0][i] = TagList[i];
			}

			int columnNum = ParsingSheet.getRow(0).getLastCellNum(); // gets the number of columnsin the header

			XSSFRow rowHeader = ParsingSheet.getRow(0);
			XSSFCell Headercell;
			for (int j = 0; j < columnNum; j++) {
				Headercell = rowHeader.getCell(j);
				String KellValue = Headercell.getStringCellValue();
				for (int i = 0; i < Limit; i++) {
					if (TagList[i].equalsIgnoreCase(KellValue)) { // This determines at what index the tags are located
																	// at for more precise searching
						TagIndex[i] = j;
					}
				}
			} // As a fun note, I've been bug testing this for so long that the word "cell"
				// looks madeup now.

			// data[i+1][TagName] = cell.toString(row.getCell(TagIndex[TagName]));
			// data[i+1][TagName] = cellToString(row.getCell(TagIndex[TagName]));
			// data[i+1][TagName] = row.getStringCellValue(row.getCell(TagIndex[TagName]);
			XSSFCell DataCell;
			for (int TagName = 0; TagName < TagList.length; TagName++) {
				for (int i = 1; i < Limit; i++) {
					XSSFRow row = ParsingSheet.getRow(i);

					DataCell = row.getCell(TagIndex[TagName]);
					data[i + 1][TagName] = DataCell.getStringCellValue();
				}
			}

			WriteToSQL(Table, data, TagList, Limit);

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void ParseFromXML(String Location, String Table, String[] TagList) throws Exception {
		try {
			File inputFile = new File(Location);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			int Limit = 50 + 1;

			String[][] data = new String[Limit][TagList.length];

			for (int i = 0; i < TagList.length; i++) // sets the first elements of data to be the Tags
			{
				data[0][i] = TagList[i];
			}

			NodeList nList = doc.getElementsByTagName("row");
			System.out.println("Start Parsing from XML");
			for (int TagName = 0; TagName < TagList.length; TagName++) {
				System.out.println(TagList[TagName]);
				for (int temp = 1; temp < Limit; temp++) // temp < nList.getLength()
				{

					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						data[temp][TagName] = eElement.getElementsByTagName(TagList[TagName]).item(0).getTextContent();
						// System.out.println(data);
					}
					// do not enable this, you will increase your runtime greatly
				}
			}
			WriteToSQL(Table, data, TagList, Limit);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void WriteToSQL(String TableName, String[][] data, String[] TagName, int Limit) throws Exception {
		try (
				Connection conn = DriverManager
						.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" + "?user=testuser"
								+ "&password=password" + "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true"
								+ "&useSSL=true");
				Statement stmt = conn.createStatement();) {

			String DropTable, CreateTable, Statement;

			DropTable = "DROP TABLE IF EXISTS " + TableName + ";";
			//System.out.println(DropTable);
			CreateTable = "CREATE TABLE " + TableName + " (" + TagName[0] + " VARCHAR(255) PRIMARY KEY, ";

			for (int j = 0; j < TagName.length - 1; j++) // creates the SWL table based on the number of strings in
															// TagName
			{
				CreateTable = CreateTable + TagName[j + 1] + " VARCHAR(255)";
				if (j != TagName.length - 2)
					CreateTable = CreateTable + ", ";
			}
			CreateTable = CreateTable + ")";
			//System.out.println(CreateTable);

			stmt.execute(DropTable);
			stmt.execute(CreateTable);

			Statement = "INSERT INTO " + TableName + " VALUES (";
			for (int j = 1; j < TagName.length; j++) // creates the Prepared Statement based on the number of strings in
														// TagName
			{
				Statement = Statement + "?, ";
			}
			Statement = Statement + "?)";

			PreparedStatement preparedStatement = conn.prepareStatement(Statement);

			for (int i = 0; i < Limit; i++) {
				for (int j = 0; j < TagName.length; j++) {
					preparedStatement.setString(j + 1, data[i][j]); // sets the Prepared String a number of times equal
																	// to the amount of strings in TagName
				}
				preparedStatement.execute();
			}
			conn.close();
		}
	}

	public static void WriteToExcel(String DataWanted, String Table, XSSFWorkbook workbook, String location)
			throws Exception {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" +
				"?user=testuser" + "&password=password" + "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true"
				+ "&useSSL=true");
				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {

			// XSSFSheet spreadsheet = workbook.createSheet("Research Data; " + location);
			// // spreadsheet object
			int sheetNum = 0;
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (workbook.getSheetAt(i).getSheetName().equals(location)) {
					workbook.removeSheetAt(i);
					sheetNum = i;
					i--;
				}
			}
			XSSFSheet spreadsheet = workbook.createSheet(location);
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

			do {
				for (int i = 1; i <= columnsNumber; i++) // this loop populates a cell with data
				{
					if (rsmd.getColumnName(i) == rsmd.getColumnName(1)) // this detects if the column at the current is
																		// equal to the first entry. If so, that means
																		// we need a new row
					{
						row = spreadsheet.createRow(rowNumber++); // This line create a new row
					}
					columnValue = rset.getString(i);
					// console.WriteLine(columnValue);
					row.createCell(i).setCellValue(columnValue);
				}
				FileOutputStream out = new FileOutputStream(new File("GFGsheet.xlsx")); // C:\Users\sleep\Desktop\Excel
				workbook.write(out);
			} while (rset.previous());

			conn.close();
		}
	}
}