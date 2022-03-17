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

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class App
{
  public static void main(String[] args) throws Exception {
	Class.forName("com.mysql.jdbc.Driver");

    newTest(); // This is what I've been building, piece by piece, to eventually replicate the old dependency Test
    //oldTest(); //This is what the program used to do when it was orginally created and modified by Dr. Aubert

  }

  public static void newTest() throws Exception {
    System.out.println("\n");
    String FilePath = EstablishFilePath() + "\\Downloads\\";
    File[] fileNames = EstablishFileList(FilePath);


	if(fileNames.length==0) {
		System.out.println("There are no databases in the downloads folder.\nPlease download at least one database and try again.");
	} 
	else {
		String fType;
		String source = "";
		String[] Table = new String[fileNames.length];
		String[] ReporterTagList = { "APPLICATION_ID", "ORG_CITY", "ORG_NAME", "PI_NAMEs" };
		String[] Search = {"Medical University of South Carolina", "ORG_NAME"};

		for (int i = 0; i < fileNames.length; i++) {
		//System.out.println("\n"+fileNames[i].getName());
		fType = FilenameUtils.getExtension(fileNames[i].getName());
		Table[i] = fType + (i + 1); // this stores the tables names in a retrievable list.
		source = fileNames[i].getName();
		
		Parsefromtxt(fileNames[i].getPath(), Table[i], "\",\"", ReporterTagList, Search, source);
		}
	}

    System.out.println("\n");

  } // ADD NEXT: SQL interaction and test putting a datalist into Excel for output.

	public static String EstablishFilePath(){
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

	public static File[] EstablishFileList(String FilePath) {
	  File f = new File(FilePath);
		
		File[] fileN = f.listFiles();
		File[] fileNames = new File[fileN.length];
		int txtCatch = 0;
		for (int i = 0; i < fileN.length; i++) {
			if (!fileN[i].getName().equals("ReadMeDownloads.txt")) {
				fileNames[txtCatch] = fileN[i];
        System.out.println(fileNames[txtCatch].getName());
				txtCatch++;
			}
		}
		return fileNames;
	}

	public static void Parsefromtxt(String txtlocation, String Table, String Delim, String[] TagList, String Search[], String source) throws Exception{
    	Scanner txtFile = new Scanner(new File(txtlocation));

		String txtFields = txtFile.nextLine();
		Scanner txtFieldsLine = new Scanner(txtFields);
		txtFieldsLine.useDelimiter(Delim);

    	int index = 0;
		int Limit = 9050 + 1;

		while (txtFile.hasNextLine()) { 
      	/*ISSUE: for some reason, the .nextLine() quits at line 333, and cuts off that line before ends. There's 356 
      	entries in the CSV file total, so this cuts out 23 entries, for some reason. Research it further. 
      	Something's weird*/
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
    	//PrintList(data);

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

			indexTracker++;

		} while (txtFile.hasNextLine() && indexTracker < Limit);

   	 	//PrintList(data);

    	if(Search != null) {
			String[][] SearchData = SearchforAttributeData(data, Search, Limit);
			//PrintList(SearchData);
			SearchData = AddTagAndData(SearchData, "Source", source);
			SearchData = AddTagAndData(SearchData, "Time_Retrieved", GetTime());
			PrintList(SearchData);
			WriteToSQL(Table, SearchData);
		} 
		else 
		{
			data = AddTagAndData(data, "Source", source);
			data = AddTagAndData(data, "Time_Retrieved", GetTime());
			PrintList(data);
			WriteToSQL(Table, data);
		}

		txtFieldsLine.close();
		txtFile.close();
		txtFields.close();
	}

	public static void PrintList(String[] input) {
		int InpLength = input.length;
		for (int j = 0; j< InpLength; j++) {
			System.out.print(input[j]+", ");
		}
	}

	public static void PrintList(String[][] input) {

		int InpLength = input.length;
		int InpDepth = input[0].length;

		for (int i = 0; i<InpLength; i++) {
			System.out.print(i + ": ");
			for (int j = 0; j<InpDepth; j++) {
				System.out.print(input[i][j]+", ");
			}
			System.out.println();
		}
	}

	public static String[][] SearchforAttributeData(String[][] data, String[] SearchParamaters, int dataLimit){

		int Length = data[0].length;
		//System.out.println(Length);
		int searchIndex = 0;
		for (int i = 0; i < Length; i++) {
			if (SearchParamaters[1].equalsIgnoreCase(data[0][i])) {
				searchIndex = i; 
			} 
		}

		int searchHits = 0;
		for (int i = 1; i<dataLimit; i++) {
			if (SearchParamaters[0].equalsIgnoreCase(data[i][searchIndex])) {
				searchHits++; 
			} //Search = {"Medical University of South Carolina", 3};
		}
		//System.out.println("Number of hits is "+searchHits);

		String[][] SearchData = new String[searchHits+1][Length];

		for (int i = 0; i < Length; i++) {
			SearchData[0][i] = data[0][i];
		}
		int max = searchHits;
		searchHits = 0;
		for (int i = 0; i<dataLimit; i++) {
			if (data[i][searchIndex].equalsIgnoreCase(SearchParamaters[0])) {
				for(int j = 0; j<Length; j++) {
					SearchData[searchHits+1][j] = data[i][j];
				}
				searchHits++;
				//System.out.println("Hit found at "+ i);
				if (searchHits == max) break;
			} 
		}
		return SearchData;
	}
	
	public static String[][] AddTagAndData(String[][] input, String Tag, String Data) throws Exception {

		int InpLength = input.length;
		int InpDepth = input[0].length;

		//System.out.println("pre-Adding input is " + InpLength + " : " + InpDepth + ". Adding " + Data);
		String[][] newList = new String[InpLength][InpDepth+1];

		newList[0][InpDepth] = Tag;
		//System.out.println("Tag is " + newList[0][InpDepth]);

		for(int i = 1; i < InpLength; i++) {
			newList[i][InpDepth] = Data;
		}

		for (int i = 0; i<InpLength; i++) {
			for (int j = 0; j<InpDepth; j++) {
				newList[i][j] = input[i][j];
			}
		}
		return newList;
	}

	public static String GetTime() {
		DateTimeFormatter Format = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
		LocalDateTime current = LocalDateTime.now();
		//System.out.println(Format.format(current));
		return (Format.format(current));
	}
  
	public static void WriteToSQL(String TableName, String[][] data) throws Exception {
		try ( Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/HW_Prospectus_DB" 
				+ "?user=testuser" + "&password=password" + "&allowMultiQueries=true" 
				+ "&createDatabaseIfNotExist=true" + "&useSSL=true"
				);
		Statement stmt = conn.createStatement();) 
		{

			String DropTable, CreateTable, Statement;

			DropTable = "DROP TABLE IF EXISTS " + TableName + ";";
			
			int Limit = data[0].length;

			String[] TagName = new String[Limit];
			for(int i = 0;i<Limit;i++){
				TagName[i] = data[0][i];
			}

			CreateTable = "CREATE TABLE " + TableName + " (" + TagName[0] + " VARCHAR(255) PRIMARY KEY, ";

			for (int j = 0; j < TagName.length - 1; j++) // creates the SWL table based on the number of strings in
															// TagName
			{
				CreateTable = CreateTable + TagName[j + 1] + " VARCHAR(255)";
				if (j != TagName.length - 2)
					CreateTable = CreateTable + ", ";
			}
			CreateTable = CreateTable + ")";
			System.out.println(CreateTable + "\n" + DropTable);

			stmt.execute(DropTable);
			stmt.execute(CreateTable);

			System.out.println("Finished dropping and creating tables.");

			Statement = "INSERT INTO " + TableName + " VALUES (";
			for (int j = 1; j < TagName.length; j++) // creates the Prepared Statement based on the number of strings in
														// TagName
			{
				Statement = Statement + "?, ";
			}
			Statement = Statement + "?)";

			PreparedStatement preparedStatement = conn.prepareStatement(Statement);

			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < Limit; j++) {
					preparedStatement.setString(j+1, data[i][j]); // sets the Prepared String a number of times equal
																	// to the amount of strings in TagName
				}
				System.out.println(preparedStatement);
				preparedStatement.execute();
			}
			conn.close();
			System.out.println("Finished");
		}
	}
	
	public static void oldTest() {
    XSSFWorkbook Wbook = new XSSFWorkbook(); // workbook object
    XSSFSheet spreadsheet = Wbook.createSheet("Research Data"); // spreadsheet object
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
    int cellid = 0;
  
    for (String key : keyid) // writing the data into the sheets
    {
      row = spreadsheet.createRow(rowid);
      Object[] objectArr = ReschData.get(key);
      cellid = 0;
      for (Object obj : objectArr) 
      {
        Cell cell = row.createCell(cellid);
        cell.setCellValue((String)obj);
        //System.out.println((String)obj);
        cellid++;
      }
      rowid++;
    } 
    
    // writing the workbook into the file
    FileOutputStream out = new FileOutputStream( new File("./GFGsheet.xlsx")); //C:\Users\sleep\Desktop\Excel
    Wbook.write(out);
    out.close();
  }
  
}
