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

import java.text.CharacterIterator;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

public class App
{
  public static void main(String[] args) throws Exception
  {
    System.out.println("\n");
    String FilePath = EstablishFilePath() + "\\Downloads\\";
    File[] fileNames = EstablishFileList(FilePath);

    for (int i = 0; i < fileNames.length; i++) {
      System.out.println("\n"+fileNames[i].getName());
      System.out.println(fileNames[i].getPath()+"\n");
      Parsefromtxt(fileNames[i].getPath(), "\",\"");
    }

    
    // writing the workbook into the file
    //FileOutputStream out = new FileOutputStream( new File("./GFGsheet.xlsx")); //C:\Users\sleep\Desktop\Excel
    //Wbook.write(out);
    //out.close();
    System.out.println("\n");

  }

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

  public static void Parsefromtxt(String txtlocation, String Delim) throws Exception{
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
			System.out.println(txtFile.nextLine()+"\n");
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
    
  }
  
  public static void OldTest() {
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
