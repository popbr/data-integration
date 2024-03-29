package popbr;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import java.io.BufferedInputStream;
import java.util.zip.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.sql.*;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.io.IOException;
import java.net.MalformedURLException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DatabaseIO {
	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");

		final long startTime = System.nanoTime();
		
		System.out.println("\n");
		XSSFWorkbook workbook = new XSSFWorkbook(); // workbook object
		String BasePath = EstablishFilePath(); // Getter for user's filepath to program
		String[] SQLElementList = { "Login", "Username", "Password" };
		String LoginPath = BasePath + File.separator + "target" + File.separator;
		// Getter for the Login information file
		
		String[] SQLLogin = DetermineSQLLogin(LoginPath);
		//String[] SQLLogin = GetLoginInfo(LoginPath, SQLElementList); // Retrieves and Stores User SQL Login information

		String FilePath = BasePath + File.separator + "target" + File.separator + "downloads" + File.separator;
		// Gets Downloads folder filepath for downloads
		File[] fileNames;

		String webScrapePath =  BasePath + File.separator + "target" + File.separator + "webpages" + File.separator;
		String[][] downloadLocationsList = {{ "NSF", "https://www.nsf.gov/awardsearch/download?DownloadFileName="}};

		/* 
		for(int h = 0; h < downloadLocationsList.length; h++) {
			if (downloadLocationsList[h][0] == "NSF") {
				for(int i = 2000; i < 2001; i++) {
					getFile(downloadLocationsList[h][1] + i + "&All=true", BasePath + i + "DB.zip");
					unzipFile( BasePath + i + "DB.zip", FilePath);
				}
			}
		} 
		*/

		try { // this catches an error when there are no files in the dowloads folder.
			fileNames = EstablishFileList(FilePath); // Creates a list of file names in the downloads folder
		} catch (Exception e) { 
			// If there are no files in the downloads folder, a blank filelist will be created and handled later
			fileNames = new File[0];
		}

		String URLPath = BasePath + File.separator + "target" + File.separator + "DBInfo.xml";
		String[] URLList = CreateURLList(URLPath);
		// PrintList(URLList); //Given that no tampering to the method occurs,
		// CreateURLList works Oct. 20th.

		//System.exit(0);

		if (fileNames.length == 0) { // This deals with the Fillearray, checking if it populated
			System.out.println(
					"There are no databases in the downloads folder.\nPlease download at least one database before running the program again.");
		} else {
			String fType;
			String source = "";
			String[] Table = new String[fileNames.length];
			// This will store the Table names for SQL information retrieval
			String[] ReporterTagList = { "APPLICATION_ID", "ORG_CITY", "ORG_NAME" };// , "PI_NAME" };
			String[][] nsfTagList = { {"AwardAmount", "Name", "SignBlockName"}, {"Award", "Institution", "ProgramOfficer"} };
			String[] tsvTagList = { "School_Name", "Location", "MD_or_DO" };
			String[] xlsTagList = { "School_Name", "Type", "State" };

			String[] Search = new String[2];
			if (args.length >= 2) {
				Search[0] = args[0];
				Search[1] = args[1];
			} else if (args.length == 1) {
				Search[0] = args[0];
				Search[1] = GetSearchInputs("What is the attribute name you would like to search for?");
			} else {

				// Search[0] = GetSearchInputs("What is the researching entity you would like to
				// search for?");
				// Search[1] = GetSearchInputs("What is the attribute name you would like to
				// search for?");
				/*
				 * there will be another else if here that checks if the user doesn't want to
				 * give any search parameters.
				 * I just haven't implemented it because it'll slow testing down if I have to
				 * type in something every time.
				 */
				Search[0] = "Medical University of South Carolina";
				Search[1] = "ORG_NAME";
			}
			// PrintList(Search);
			// System.exit(0);

			String[][] ParsingData;
			int[] PKAddition = { 1, 2 };
			int Limit = 50;
			// This denotes what additional Primary Keys there are for a file, given by the
			// position in the input statements (index of 1, 2, etc...) tsvTagList = null;

			CreateDatabase(SQLLogin);
			CreateLinkageTable(SQLLogin);
			// this starts up the linkage table in SQL that we use to link people to their
			// data across the databases

			for (int i = 0; i < fileNames.length; i++) {
				// Goes through the list of files and parses from each of them, delegating to
				// the appropriate method based on the file extension
				fType = FilenameUtils.getExtension(fileNames[i].getName());
				// Getter for file extension of the current file
				Table[i] = fType + (i + 1); // this stores the tables names in a retrievable list.
				source = fileNames[i].getName(); // Getter for the source of the data file
				if (fType.equals("xml")) {
					ParsingData = ParsefromSingleXML(fileNames[i].getPath(), Table[i], nsfTagList, null, source, SQLLogin, null, Limit);
					//LinkTable(ParsingData, Table[i], SQLLogin, ReporterTagList);
				} else if (fType.equals("csv")) {
					ParsingData = Parsefromtxt(fileNames[i].getPath(), Table[i], "\",\"", ReporterTagList, null, source, SQLLogin, null, Limit);
					LinkTable(ParsingData, Table[i], SQLLogin, ReporterTagList);
				} else if (fType.equals("tsv")) {
					ParsingData = Parsefromtxt(fileNames[i].getPath(), Table[i], "	", tsvTagList, null, source, SQLLogin, PKAddition, Limit);
					LinkTable(ParsingData, Table[i], SQLLogin, tsvTagList);
				} else if (fType.equals("xlsx")) {
					ParsingData = ParsefromExcel(fileNames[i].getPath(), Table[i], xlsTagList, null, source, SQLLogin, PKAddition, Limit);
					LinkTable(ParsingData, Table[i], SQLLogin, xlsTagList);
				}

			}
			Table[0] = "LinkTable";
			// FindSimilarRelation("xlsx1", "tsv2", "School_Name", SQLLogin, workbook);
			// WriteToExcel("*", Table, workbook, SQLLogin);

		}

		System.out.println("Finished");
		final long duration = System.nanoTime() - startTime;
		System.out.println(duration);
		System.exit(0);
		// Exits the program. Without it the program will stall for ~15 seconds once "finished"
		System.out.println("\n");

	}

	public static String EstablishFilePath() throws Exception {
		// returns the current filepath of the program by creating a temp file and
		// getting that file's filepath
		File s = new File("f.txt");
		String FilePath = "";
		char[] tempChar = s.getAbsolutePath().toCharArray();
		char[] newChar = new char[tempChar.length - 6];
		for (int i = 0; i < newChar.length; i++) { // Gets the filepath without have "f.text" in the filepath.
			newChar[i] = tempChar[i];
		}
		FilePath = String.valueOf(newChar);
		// System.out.println(FilePath);
		return FilePath;
	}

	public static File[] EstablishFileList(String FilePath) throws Exception {
		// Returns all files at a given destination
		File f = new File(FilePath);
		File[] fileN = f.listFiles();
		File[] fileNames = new File[fileN.length];
		int txtCatch = 0;
		for (int i = 0; i < fileN.length; i++) {
			if (!fileN[i].getName().equals("ReadMeDownloads.txt")) {
				fileNames[txtCatch] = fileN[i];
				// System.out.println(fileNames[txtCatch].getName());
				txtCatch++;
			}
		}
		return fileNames;
	}

	public static String[] CreateURLList(String URL) throws Exception {

		String[] URLElements = { "DB", "Name", "URL" };
		String[] URLList = GetURLList(URL, URLElements);
		return URLList;
	}
	
	public static void getFile(String fileURL, String destination) {
        System.out.println("Retrieving file...");
        try (BufferedInputStream in = new BufferedInputStream(new URL(fileURL).openStream());          
            FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzipFile(String fileURL, String destination) {
        System.out.println("Unzipping file...");
        try {
            String fileZip = fileURL;
            File destDir = new File(destination);
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    
                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
    
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
    
        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
    
        return destFile;
    }

	public static String[][] Parsefromtxt(String txtlocation, String Table, String Delim, String[] TagList,
			String Search[], String source, String[] SQLLogin, int[] PKAddition, int Limit) throws Exception {
		// This parses information from a txt file, typically a TSV or CSV
		Scanner txtFile = new Scanner(new File(txtlocation)); // Opens a text scanner on the file in question

		String txtFields = txtFile.nextLine();
		Scanner txtFieldsLine = new Scanner(txtFields);
		txtFieldsLine.useDelimiter(Delim);
		// Sets the delimiter to a tab, comma, etc... based on the delimeter passed
		// through

		int index = 0;

		while (txtFile.hasNextLine()) { // Gets the total amount of lines/data entries in a file
			/*
			 * ISSUE: for some reason, the .nextLine() quits at line 333, and cuts off that
			 * line before ends. There's 356
			 * entries in the CSV file total, so this cuts out 23 entries, for some reason.
			 * Research it further. Something's weird
			 */
			index++;
			txtFile.nextLine();
		}
		if (index < Limit) // if the total amount of file entries/lines in a file if less than the limit
							// given, then set the limit to that total amount.
			Limit = (index + 1); // Without this, the program will try to read lines that don'texist, and throw
									// an exception

		index = 0;
		txtFile.reset();
		txtFile = new Scanner(new File(txtlocation));

		txtFields = txtFile.nextLine();
		txtFieldsLine = new Scanner(txtFields);
		txtFieldsLine.useDelimiter(Delim);

		int indexTracker = 0;
		String currentWord = "";
		String currentLine = "";
		int length;
		if (TagList != null) // If no tags are passed to the method, then the program takes the first 10
								// attributes it finds
			length = TagList.length;
		else
			length = 10; // length is the basis for how many attributes the program scrapes

		int[] TagIndex = new int[length]; // This is where the attribute names are stored

		for (int i = 0; i < length; i++) { // This is an array of numbers... Yes, it's needed. The program breaks
											// without it. No, I can't recall why.
			TagIndex[i] = i;
		}
		String[][] data = new String[Limit][length]; // Here is the list of Attribute data that will be passedto SQL

		if (TagList != null) { // This makes the first row the Attribute names. If there were no attributes
								// specified, ituses a generic "Attribute1" schema
			for (int i = 0; i < length; i++) {
				data[0][i] = TagList[i];
			}
			// PrintList(data);
		} else {
			for (int i = 0; i < length; i++) {
				data[0][i] = "Attribute" + i;
			}
		}

		while (txtFieldsLine.hasNext()) // This goes through the first line of a file and gets the position of the
										// attributes wanted (I think. This while loop confuses me a little)
		{
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

			if (TagList != null) { // This finds the attributes we want and stores them
				for (int p = 0; p < length; p++) {
					if (currentWord.equalsIgnoreCase(TagList[p])) {
						TagIndex[indexTracker] = index;
						indexTracker++;
					}
				}
			}
			index++;
		}

		indexTracker = 1;

		do { // This brings a new entry/line to be parsed through. Note the "Do/While" makes
				// sure to bring down the first entry, and not skip it
			index = 0;
			currentLine = txtFile.nextLine();
			txtFieldsLine = new Scanner(currentLine);
			txtFieldsLine.useDelimiter(Delim);
			char quote = '"';

			while (txtFieldsLine.hasNext()) {
				currentWord = txtFieldsLine.next();
				// System.out.println(currentWord);
				if (index == 0) { // Makes sure the first data piece will not has a " at the front
					char[] tempChar = currentWord.toCharArray();
					if (tempChar[0] == quote) {
						char[] newChar = new char[tempChar.length - 1];

						for (int i = 1; i < newChar.length + 1; i++) {
							newChar[i - 1] = tempChar[i];
						}
						currentWord = String.valueOf(newChar);
					}
				}

				if (index == (length - 1)) { // makes sure the last data piece will no have a " at the end
					char[] tempChar = currentWord.toCharArray();
					if (tempChar == null || tempChar.length == 0) { // this deals with entries such as " , ;"
						currentWord = "null";
					} else {
						char[] newChar = new char[tempChar.length - 1];
						for (int i = 0; i < newChar.length; i++) {
							newChar[i] = tempChar[i];
						}
						currentWord = String.valueOf(newChar);
					}
				}

				for (int ind = 0; ind < TagIndex.length; ind++) { 
					// Checks to see if the index/attribute data column the reader 
					// is on is the same as one of the attribute.
					// System.out.println(TagIndex[ind]); //If so, then this current word is data
					// the program wants, and it stores that data in the list that gets passed into
					// SQL
					if (index == TagIndex[ind]) {
						data[indexTracker][ind] = currentWord;
					}
				}
				index++;
			}

			indexTracker++;

		} while (txtFile.hasNextLine() && indexTracker < Limit);

		txtFieldsLine.close();
		txtFile.close();
		//PrintList(data);

		if (Search != null) { 
			// If no search item was passed (like a specific school), then the program puts the entire datalist into SQL. 
			//Else, the program combs through the data for that search item, and passed only that data to SQL
			String[][] SearchData = SearchforAttributeData(data, Search, Limit); // This outputs a new list of data
																					// relating only to that search item
			// PrintList(SearchData);
			SearchData = AddTagAndData(SearchData, "Source", source); // Adds a source attribute
			SearchData = AddTagAndData(SearchData, "Time_Retrieved", GetTime());// Adds a Time retrieved attribute
			// PrintList(SearchData);
			WriteToSQL(Table, SearchData, SQLLogin, PKAddition); // Inputs the searched list into SQL
			return SearchData;
		} else {
			// data = AddTagAndData(data, "Source", source);
			// data = AddTagAndData(data, "Time_Retrieved", GetTime());
			// PrintList(data);
			WriteToSQL(Table, data, SQLLogin, PKAddition); // Inputs the whole data list into SQL
			return data;
		}
	}

	public static String[][] ParsefromExcel(String ExcelLocation, String Table, String[] TagList, String Search[],
			String source, String[] SQLLogin, int[] PKAddition, int Limit) throws Exception {
		try {
			// String WorkbookName = ExcelLocation.getName();
			OPCPackage pkg = OPCPackage.open(new File(ExcelLocation)); 
			// These lines find the Excel file, workbook, and work sheet
			XSSFWorkbook ParsingWorkbook = new XSSFWorkbook(pkg);
			XSSFSheet ParsingSheet = ParsingWorkbook.getSheetAt(0);

			int foundLimit = 0;
			if(Limit <= 0) {
			Limit = ParsingSheet.getLastRowNum() + 1; 
			} else 
			// Since Excel starts at 1, and this program starts at 0, this will make sure the last row isn't skipped over

			System.out.println(Limit);

			int[] TagIndex = new int[TagList.length]; // This is where the attribute names are stored

			String[][] data = new String[Limit][TagList.length]; 
			// Here is the list of Attribute data that will be passed to SQL

			for (int i = 0; i < TagList.length; i++) { // This makes the first row the Attribute names.
				data[0][i] = TagList[i];
			}

			// PrintList(data);

			int columnNum = ParsingSheet.getRow(0).getLastCellNum(); // gets the number of columns in the header

			XSSFRow rowHeader = ParsingSheet.getRow(0); // Sets the row to the first one on the Excel Sheet
			XSSFCell Headercell;
			String cellValue = "";

			for (int j = 0; j < columnNum; j++) { 
				// this goes through each column and finds the name. If it's in the taglist, then the program notes its position
				Headercell = rowHeader.getCell(j);
				cellValue = Headercell.getStringCellValue();
				for (int i = 0; i < TagList.length; i++) {
					if (TagList[i].equalsIgnoreCase(cellValue)) { 
						// This determines at what index the tags are located at for more precise searching
						TagIndex[i] = j;
					}
				}
			} // As a fun note, I've been bug testing this for so long that the word "cell" looks made-up now.

			XSSFCell DataCell;
			XSSFRow row;
			cellValue = "";
			for (int TagName = 0; TagName < TagList.length; TagName++) { 
				// This gets the data from the cells, row by row, only if they're 
				// under the same row as an attribute wanted.
				for (int i = 1; i < Limit; i++) {
					row = ParsingSheet.getRow(i);
					DataCell = row.getCell(TagIndex[TagName]);
					cellValue = DataCell.getStringCellValue();
					data[i][TagName] = cellValue;
				}
			}

			// PrintList(data);

			WriteToSQL(Table, data, SQLLogin, PKAddition); // Writes the data gathered to SQL
			return data;
		}

		catch (Exception e) {
			e.printStackTrace(); // Stacktrace for if a crash occurs.
			return null;
		}
	}

	public static String[][] ParsefromSingleXML(String Location, String Table, String[][] TagList, String Search[], String source, String[] SQLLogin, int[] PKAddition, int Limit) {
		try {
			System.out.println("Start Parsing from XML");

			String[][] data = new String[1][TagList[1].length]; 

			System.out.println(TagList[1].length);

			for(int i = 0 ; i<TagList[1].length; i++) { // this goes through a files and searches for the tags and their data
				data[0][i] = ParseXMLTag(Location, TagList[0][i], TagList[1][i]);
			}
			//PrintList(data);
			

			if (Search != null) { 
				// If no search item was passed (like a specific school), then the program puts the entire datalist into SQL. 
				//Else, the program combs through the data for that search item, and passed only that data to SQL
				data = SearchforAttributeData(data, Search, Limit);
				// PrintList(data);
				data = AddTagAndData(data, "Source", source); // Adds a source attribute
				data = AddTagAndData(data, "Time_Retrieved", GetTime()); // Adds a Time retrieved attribute
				WriteToSQL(Table, data, SQLLogin, null); // Inputs the searched list into SQL
				return data;
			} else {
				data = AddTagAndData(data, "Source", source); // Adds a source attribute
				data = AddTagAndData(data, "Time_Retrieved", GetTime()); // Adds a Time retrieved attribute
				// PrintList(data);
				WriteToSQL(Table, data, SQLLogin, null); // Inputs the whole data list into SQL
				return data;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String ParseXMLTag(String Location, String element, String tag) {
		try {
			File inputFile = new File(Location); 
			// This gets the file's location, starts up the XML reader, and normalizes the file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(tag);

			String data = "";
			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node dataElement;
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) 
				{
					Element eElement = (Element) nNode;

					dataElement = eElement.getElementsByTagName(element).item(0);
					if (dataElement != null) 
					{
						data = dataElement.getTextContent(); // maybe encapsul this in a try catch?
					} 
					else data = "NO_DATA_FOUND";
				}
			}
			return data;

		}	catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String[][] ParsefromXML(String Location, String Table, String[] TagList, String Search[], String source, String[] SQLLogin, int[] PKAddition, int Limit) throws Exception {
		try {
			File inputFile = new File(Location); 
			// This gets the file's location, starts up the XML reader, and normalizes the file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			//Limit = 50 + 1;

			String[][] data = new String[Limit][TagList.length]; 
			// Here is the list of Attribute data that will be passed to SQL

			for (int i = 0; i < TagList.length; i++) { // sets the first elements of data to be the Tags
				data[0][i] = TagList[i];
			}

			NodeList nList = doc.getElementsByTagName("row");
			System.out.println("Start Parsing from XML");

			Node dataElement;

			for (int TagName = 0; TagName < TagList.length; TagName++) {
				// System.out.println(TagList[TagName]);
				for (int temp = 1; temp < Limit; temp++) // temp < nList.getLength()
				{
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;

						dataElement = eElement.getElementsByTagName(TagList[TagName]).item(0);
						if (dataElement != null) {
							data[temp][TagName] = dataElement.getTextContent(); // maybe encapsul this in a try catch?
						} else
							data[temp][TagName] = null;
					}
				}
			}
			// PrintList(data);

			if (Search != null) { 
				// If no search item was passed (like a specific school), then the program puts the entire datalist into SQL. 
				//Else, the program combs through the data for that search item, and passed only that data to SQL
				data = SearchforAttributeData(data, Search, Limit);
				// PrintList(data);
				data = AddTagAndData(data, "Source", source); // Adds a source attribute
				data = AddTagAndData(data, "Time_Retrieved", GetTime()); // Adds a Time retrieved attribute
				WriteToSQL(Table, data, SQLLogin, null); // Inputs the searched list into SQL
				return data;
			} else {
				data = AddTagAndData(data, "Source", source); // Adds a source attribute
				data = AddTagAndData(data, "Time_Retrieved", GetTime()); // Adds a Time retrieved attribute
				// PrintList(data);
				WriteToSQL(Table, data, SQLLogin, null); // Inputs the whole data list into SQL
				return data;
			}

		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String[] GetLoginInfo(String Location, String[] Elements) throws Exception { 
		// returns the login info of the specified type
		File inputFile = new File(Location); 
		// This gets the file's location, starts up the XML reader, and normalizes the file
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName(Elements[0]); // Looks at the first element
		Node Info;

		String[] LoginInfo = new String[(Elements.length - 1)]; // readys the login string to be filled and passed
		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) { // Goes to the element of the type wanted, like SQL
				Element eElement = (Element) nNode;
				for (int k = 1; k < (Elements.length); k++) {
					Info = eElement.getElementsByTagName(Elements[k]).item(0); 
					// Gets and Sets all the specified tags, like Username and Password
					LoginInfo[(k - 1)] = Info.getTextContent();
				}
				// <a href="download?DownloadFileName=2021&amp;All=true">
			}
		}
		// PrintList(LoginInfo);

		return LoginInfo; // returns the login information
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
	
	public static String[] GetURLList(String Location, String[] Elements) throws Exception { 
		// returns the login info of the specified type

		File inputFile = new File(Location); 
		// This gets the file's location, starts up the XML reader, and normalizes the file
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(inputFile);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName(Elements[0]); // Looks at the first element
		Node Info;

		String[] URLInfo = new String[(Elements.length - 1) * nList.getLength()]; 
		// readys the login string to be filled and passed
		int loop;

		for (int temp = 0; temp < nList.getLength(); temp++) {
			loop = (nList.getLength() - 1) * temp;
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) { // Goes to the element of the type wanted, like SQL
				Element eElement = (Element) nNode;
				for (int k = 1; k < Elements.length; k++) {
					Info = eElement.getElementsByTagName(Elements[k]).item(0); // Gets and Sets all the specified tags,
																				// like Username and Password
					URLInfo[(k - 1) + (loop)] = Info.getTextContent();
				}

				// <a href="download?DownloadFileName=2021&amp;All=true">
			}
		}
		// PrintList(URLInfo);
		return URLInfo; // returns the login information
	}

	public static void PrintList(int[] input) throws Exception { // Prints an array of integers
		int InpLength = input.length;
		for (int j = 0; j < InpLength; j++) {
			System.out.println(input[j] + ", ");
		}
	}

	public static void PrintList(String[] input) throws Exception { // Prints an array of Strings
		int InpLength = input.length;
		for (int j = 0; j < InpLength; j++) {
			System.out.println(input[j] + ", ");
		}
	}

	public static void PrintList(String[][] input) throws Exception { // Prints a double array of Strings

		int InpLength = input.length;
		int InpDepth = input[0].length;

		for (int i = 0; i < InpLength; i++) {
			System.out.print(i + ": ");
			for (int j = 0; j < InpDepth; j++) {
				System.out.print(input[i][j] + ", ");
			}
			System.out.println();
		}
	}

	public static String[][] FillEmpty(String[][] input) throws Exception {
		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[0].length; j++) {
				// if(input[i][j].equals("") || input[i][j].equals("\t"))
				// input[i][j].equals("No_Data_Found");
				if (input[i][j]==null || input[i][j] == "") {
					input[i][j] = "No_Data_Found";
				}
			}
		}
		return input;
	}

	public static String[][] SearchforAttributeData(String[][] data, String[] SearchParamaters, int dataLimit)
			throws Exception {
		// Searches a passed array for the specific data in the array, and returns a new
		// array of only that data

		int Length = data[0].length;
		// System.out.println(Length);
		int searchIndex = 0;
		for (int i = 0; i < Length; i++) {
			if (SearchParamaters[1].equalsIgnoreCase(data[0][i])) {
				searchIndex = i;
			}
		}

		int searchHits = 0; // Goes through and finds the number of matches to the search parameter. Once
							// done, it creates an array with a length of the enumber of hits in the
							// original array
		for (int i = 1; i < dataLimit; i++) {
			if (SearchParamaters[0].equalsIgnoreCase(data[i][searchIndex])) {
				searchHits++;
			} // Search = {"Medical University of South Carolina", 3};
		}
		// System.out.println("Number of hits is "+searchHits);

		String[][] SearchData = new String[searchHits + 1][Length]; // Creates the new array to be returned

		for (int i = 0; i < Length; i++) {
			SearchData[0][i] = data[0][i]; // Fills the top row of the new array with the attribure names
		}

		int max = searchHits;
		searchHits = 0;
		for (int i = 0; i < dataLimit; i++) { // fills the new array with the data pertaining only to the search
												// parameter
			if (data[i][searchIndex].equalsIgnoreCase(SearchParamaters[0])) {
				for (int j = 0; j < Length; j++) {
					SearchData[searchHits + 1][j] = data[i][j];
				}
				searchHits++;
				// System.out.println("Hit found at "+ i);
				if (searchHits == max)
					break;
			}
		}
		return SearchData;
	}

	public static String[] AddRow(String[] data) {
		String[] newString = new String[data.length+1];
		for(int i = 0; i < data.length; i++) {
			newString[i] = data[i];
		}
		return newString;
	}

	public static String[][] AddTagAndData(String[][] input, String Tag, String Data) throws Exception {
		// adds an attribute and data to each row of a passed array input, the outputs a modified array
		int InpLength = input.length;
		int InpDepth = input[0].length;

		// System.out.println("pre-Adding input is " + InpLength + " : " + InpDepth + ".
		// Adding " + Data);
		String[][] newList = new String[InpLength][InpDepth + 1];

		newList[0][InpDepth] = Tag;
		// System.out.println("Tag is " + newList[0][InpDepth]);

		for (int i = 1; i < InpLength; i++) {
			newList[i][InpDepth] = Data;
		}

		for (int i = 0; i < InpLength; i++) {
			for (int j = 0; j < InpDepth; j++) {
				newList[i][j] = input[i][j];
			}
		}
		return newList;
	}

	public static String GetSearchInputs(String Question) throws Exception {

		String Results = "";
		Scanner myObj = new Scanner(System.in); // Create a Scanner object

		System.out.println(Question);
		Results = myObj.nextLine(); // Reads input for Search entity

		/*
		 * I know asking users to know thew attribute type is too much.
		 * What I'm thinking is list general attributes, like ORG_Name,
		 * for them to copy use. is there a better way you might suggest?
		 */
		return Results;
	}

	public static String GetTime() throws Exception { // Gets the time when called.
		DateTimeFormatter Format = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
		// Alternate date format: "dd/MM/yyyy HH:mm:ss"
		LocalDateTime current = LocalDateTime.now();
		// System.out.println(Format.format(current));
		return (Format.format(current));
	}

	public static void WriteToSQL(String TableName, String[][] data, String[] SQLLogin, int[] PKAdditions)
			throws Exception {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DatabaseIO"
				+ "?user=" + SQLLogin[0] + "&password=" + SQLLogin[1] + "&allowMultiQueries=true"
				+ "&createDatabaseIfNotExist=true" + "&useSSL=true");
				Statement stmt = conn.createStatement();) 
			{

			data = FillEmpty(data);
			String DropTable, CreateTable, Statement;
			DropTable = "DROP TABLE IF EXISTS " + TableName + ";"; // We remove the previous table if it exists.
			// Cf. discussion at https://github.com/popbr/data-integration/issues/12

			int Limit = data[0].length; // Sets the amount of attributes to be added

			String[] TagName = new String[Limit];
			for (int i = 0; i < Limit; i++) { // gets the attribute names
				TagName[i] = data[0][i];
				//System.out.print("TagName:" + TagName[i] + "\n data: " + data[0][i] + "\n");
			}

			CreateTable = "CREATE TABLE " + TableName + " (Position INT NOT NULL AUTO_INCREMENT, EntryID INT, "; 
			// Creates the Create Table command.
			// Implicitly, the command has a Table name and 1 attribute to be added. More are added as necessary, as seen below
			for (int j = 0; j < TagName.length; j++) {// creates the SQL table based on the number of strings in TagName
				CreateTable += TagName[j] + " TEXT";
				if (j != TagName.length - 1)
					CreateTable += ", ";
			}

			String AddPK = "Position"; 
			// This begins to set to Primary Keys. Automatically, the first attribute is made a PK

			/*
			 * if (PKAdditions != null) { // This sets primary Keys too, based on the PKAdditions list passed
			 * for (int num : PKAdditions) { // This can't handle adding more than 1 PK right now
			 * AddPK += ", " + TagName[num];
			 * //System.out.println(AddPK);
			 * }
			 * }
			 */
			CreateTable = CreateTable + ", PRIMARY KEY (" + AddPK + "))"; 
			// This adds the PK attributes to the create Table command
			// System.out.println(CreateTable + "\n" + DropTable);

			stmt.execute(DropTable); // Drops the current table, if it exists
			stmt.execute(CreateTable); // Creates the current table

			Statement = "INSERT INTO " + TableName + "(";
			for (int j = 0; j < TagName.length; j++) {
				// creates the Prepared Statement based on the number of strings in TagName
				Statement += TagName[j];
				if (j != TagName.length - 1)
					Statement += ", ";
			}
			Statement += ") VALUES(";
			for (int j = 0; j < TagName.length - 1; j++) {
				// creates the Prepared Statement based on the number of strings in TagName
				Statement += "?, ";
			}

			Statement = Statement + "?)"; // finishes the PS, and readies it for data to be added to it.
			// System.out.println(Statement + "\n");
			// PrintList(TagName);
			PreparedStatement preparedStatement = conn.prepareStatement(Statement);

			for (int i = 0; i < data.length; i++) { 
				// This adds data to the Prepared String and executes it, one row at a time
				for (int j = 0; j < Limit; j++) {
					preparedStatement.setString(j + 1, data[i][j]);
					// sets the Prepared String a number of times equal to the amount of strings in TagName
				}
				// System.out.println(preparedStatement);
				preparedStatement.execute();
			}

			conn.close();
		}
	}

	public static void FindSimilarRelation(String Table1, String Table2, String Attribute, String[] SQLLogin,
			XSSFWorkbook workbook) throws Exception {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DatabaseIO"
				+ "?user=" + SQLLogin[0] + "&password=" + SQLLogin[1] + "&allowMultiQueries=true"
				+ "&createDatabaseIfNotExist=true" + "&useSSL=true");
				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
			XSSFSheet spreadsheet = workbook.createSheet("SimilarData");
			// This creates a datasheet for the data to be put into
			XSSFRow row; // creating a row object

			System.out.println("Writing Similar Relations to Excel.");

			int rowNumber = 0;
			row = spreadsheet.createRow(0); // Creates a new row in the final Excel Sheet
			String Table1Att = Table1 + "." + Attribute;
			String Table2Att = Table2 + "." + Attribute;
			String Table2Id = "data2";
			String Table2DataId = Table2Id + "." + Attribute;
			String Statement;

			Statement = "SELECT " + Table1Att + " FROM " + Table1 + " WHERE " + Table1Att + " IN ( SELECT " + Table2Att
					+ " FROM " + Table2 + " WHERE " + Table2Att + " IS NOT NULL);";

			ResultSet rset = stmt.executeQuery(Statement); 
			// Requests the attribute/Attribute Data in Table 1 that also appears in Table 2

			ResultSetMetaData rsmd = rset.getMetaData(); 
			// This parses through the data and outputs in to the Excel sheet, row by row
			int columnsNumber = rsmd.getColumnCount();
			String columnValue = "";

			String ColumnName = rsmd.getColumnName(1);

			if (rowNumber > 0 && true) { // this create a line between entries
				row = spreadsheet.createRow(rowNumber++);
			}
			rset.last();

			do {
				for (int i = 1; i <= columnsNumber; i++) // this loop populates a cell with data
				{
					columnValue = rset.getString(i);
					if (rsmd.getColumnName(i) == rsmd.getColumnName(1)) { 
						// this detects if the column at the current is equal to the first entry. 
						// If so, that means we need a new row.
						row = spreadsheet.createRow(rowNumber++); // This line create a new row
					}
					// System.out.println(columnValue);
					row.createCell(i).setCellValue(columnValue);
				}
				FileOutputStream out = new FileOutputStream(new File("GFGsheet.xlsx")); // C:\Users\sleep\Desktop\Excel
				workbook.write(out);
			} while (rset.previous());

			conn.close();
		}
	}

	public static void CreateDatabase(String[] SQLLogin) throws Exception {
		try (Connection conn = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/DatabaseIO" + "?user=" + SQLLogin[0]
					+ "&password=" + SQLLogin[1] + "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true" + "&useSSL=true");
				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {

			String DropTable, CreateTable;
			DropTable = "DROP Database DatabaseIO;";
			CreateTable = "CREATE Database DatabaseIO;";

			stmt.executeUpdate(DropTable);
			stmt.executeUpdate(CreateTable);
		}
	}

	public static void CreateLinkageTable(String[] SQLLogin) throws Exception {
		try (Connection conn = DriverManager
				.getConnection("jdbc:mysql://localhost:3306/DatabaseIO" + "?user=" + SQLLogin[0]
					+ "&password=" + SQLLogin[1] + "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true" + "&useSSL=true");
				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
			String DropTable, CreateTable, InitInsert = "INSERT INTO LinkTable(NAME) VALUES(?)", InitValue = "Test";
			PreparedStatement preparedStatement = conn.prepareStatement(InitInsert);

			DropTable = "DROP TABLE IF EXISTS LinkTable;";
			CreateTable = "CREATE TABLE LinkTable (UID INT AUTO_INCREMENT PRIMARY KEY, NAME TEXT);";
			// Creates the Create Table command.

			stmt.execute(DropTable); // Drops the current table, if it exists
			stmt.execute(CreateTable); // Creates the current table
			preparedStatement.setString(1, InitValue);
			preparedStatement.execute();

		} catch (Exception e) {
			e.printStackTrace(); // Stacktrace for if a crash occurs.
		}
	}

	public static void LinkTable(String[][] ParsingData, String CurrentTable, String[] SQLLogin, String[] TagList) throws Exception {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DatabaseIO" + "?user=" + SQLLogin[0] 
			+ "&password=" + SQLLogin[1] + "&allowMultiQueries=true" + "&createDatabaseIfNotExist=true" + "&useSSL=true");
				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
			String Test = "Columbia University", TagName = "", ColumnValueName = "", AddFK = "", AddID = "";
			String FKInsert = "INSERT INTO LinkTable(NAME) VALUES(?)";
			PreparedStatement preparedStatement = conn.prepareStatement(FKInsert);
			int ColumnsNumber = 0, columnMax, ColumnValueTableID = 0, InpLength = ParsingData.length, TagIndex = -1, length = 0;
			boolean noMatch = false;

			for (int tag = 0; tag < TagList.length; tag++) {
				if (TagList[tag].equals("School_Name")) {
					TagName = "School_Name";
					TagIndex = tag;
				} else if (TagList[tag].equals("ORG_NAME")) {
					TagName = "ORG_NAME";
					TagIndex = tag;
				}
			}

			System.out.println("Starting Linkage: " + InpLength);

			ResultSet rsetCount = stmt.executeQuery("SELECT COUNT(NAME) FROM LinkTable;");
			rsetCount.next();
			columnMax = rsetCount.getInt(1);
			//System.out.println(columnMax);
			// This is where the initial length of the LinkTable is established

			ResultSet rset = stmt.executeQuery("SELECT NAME, UID FROM LinkTable;");
			// This selects the Name attribute from the Linktable Database to compare to the names recently found in files.
			rset.next();
			String[] LinkData = new String[columnMax];

			do {

				ColumnValueName = rset.getString(1);
				ColumnValueTableID = rset.getInt(2);

				LinkData[ColumnsNumber] = ColumnValueName;

				ColumnsNumber++;
			} while (rset.next()); 

			rset.close();

			//PrintList(LinkData);
			for (int k = 1; k < InpLength; k++) {
				//This iterates  every entity in the parsing data 
				length = LinkData.length;
				noMatch = true;

				for(int l = 0; l < length; l++) 
				{
					if (ParsingData[k][TagIndex] == LinkData[l]) {

						rset = stmt.executeQuery("SELECT NAME, UID FROM LinkTable WHERE NAME = \"" + ParsingData[k][TagIndex] + "\";");
						rset.next();
						ColumnValueTableID = rset.getInt(2);
						AddID = "Update " + CurrentTable + " SET EntryID = " + ColumnValueTableID + " WHERE " + TagName + " = \"" + ParsingData[k][TagIndex] + "\";";
						stmt.execute(AddID);
						rset.close();

						noMatch = false;
						break;
					} 
				}	

				if(noMatch) {

					preparedStatement.setString(1, ParsingData[k][TagIndex]);
					preparedStatement.execute(); // "INSERT INTO LinkTable(NAME) VALUES(?)";

					rset = stmt.executeQuery("SELECT NAME, UID FROM LinkTable WHERE NAME = \"" + ParsingData[k][TagIndex] + "\";");
					rset.next();
					ColumnValueTableID = rset.getInt(2);
					AddID = "Update " + CurrentTable + " SET EntryID = " + ColumnValueTableID + " WHERE " + TagName + " = \"" + ParsingData[k][TagIndex] + "\";";
					stmt.execute(AddID);
					rset.close();

					LinkData = AddRow(LinkData);
					LinkData[LinkData.length-1] = ParsingData[k][TagIndex];	

					noMatch = false;

					//System.out.println("\n" + k + ";");
					//PrintList(LinkData);
				}
						
			} 
			
			AddFK = "ALTER TABLE " + CurrentTable + " ADD FOREIGN KEY (EntryID) REFERENCES LinkTable (UID);";
			stmt.executeUpdate(AddFK);

			System.out.println("\n");
			//PrintList(LinkData);		
		} // UID INT PRIMARY KEY, NAME VARCHAR(255), EMAIL VARCHAR(255))

		catch (Exception e) {
			e.printStackTrace(); // Stacktrace for if a crash occurs.
		}

	}

	public static void WriteToExcel(String DataWanted, String[] Table, XSSFWorkbook workbook, String[] SQLLogin)
			throws Exception {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/DatabaseIO"
				+ "?user=" + SQLLogin[0] + "&password=" + SQLLogin[1] + "&allowMultiQueries=true"
				+ "&createDatabaseIfNotExist=true" + "&useSSL=true");
				Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {

			XSSFSheet spreadsheet = workbook.createSheet("Data");
			XSSFRow row; // creating a row object
			String source;

			System.out.println("Starting writing to Excel.");

			int rowNumber = 0;
			row = spreadsheet.createRow(0); // This creates a new sheet
			for (int q = 0; q < Table.length; q++) {

				// System.out.println(Table[q]);
				String strSelect = "SELECT " + DataWanted + " FROM " + Table[q]; // This selects the data from SQL
				ResultSet rset = stmt.executeQuery(strSelect);

				ResultSetMetaData rsmd = rset.getMetaData(); // This gets the retrieved data ready to be read
				int columnsNumber = rsmd.getColumnCount();
				String columnValue = "";

				String ColumnName = rsmd.getColumnName(1);

				if (rowNumber > 0 && true) { // this create a line between entries
					row = spreadsheet.createRow(rowNumber++);
				}
				rset.last();

				do {
					for (int i = 1; i <= columnsNumber; i++) // this loop populates a cell with data
					{
						columnValue = rset.getString(i);
						if (rsmd.getColumnName(i) == rsmd.getColumnName(1)) { 
							// this detects if the column at the current is equal to the first entry.
							// If so, that means we need a new row.
							row = spreadsheet.createRow(rowNumber++); // This line create a new row
						}
						// System.out.println(columnValue);
						row.createCell(i).setCellValue(columnValue);
					}

					FileOutputStream out = new FileOutputStream(new File("GFGsheet.xlsx")); // C:\Users\sleep\Desktop\Excel
					workbook.write(out);
				} while (rset.previous());
			}
			conn.close();
		}
	}
}
