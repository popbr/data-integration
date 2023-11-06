package popbr;
// mvn exec:java -Dexec.mainClass="popbr.AbstractFinder"

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;

import java.sql.*;

import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

/*
// Those classes clash with Jsoup's classes, 
// I am commenting them out for now,
// as well as the methods using them.
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
*/

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

// Documentation at 
// https://jsoup.org/apidocs/org/jsoup/nodes/Document.html
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AbstractFinder {
    public static void main(String[] args) throws Exception { 
       
       System.out.println(EstablishFilePath()); //need to figure out exactly what to do
       // can improve this a bit maybe, will ask
       System.out.println("Welcome to Abstract Finder.");

       ArrayList<String> searchList = Read_From_Excel(); // will return a searchList that has the author's name and all of the titles for our search query
    
       ArrayList<String> abstractList = RetrieveAbstract(searchList); //takes a few minutes to accomplish due to having to search on the Internet    

       Write_To_Excel(abstractList); // Currently only does one sheet at a time and needs to be manually updated

       System.out.println("Thanks for coming! Your abstracts should be in your Excel file now");

       System.exit(0);
    }
    
    public static ArrayList<String> RetrieveAbstract(ArrayList<String> searchFor)
    {
    
       String abstracttext = " "; // Will be overwritten by the abstract if we succeed.
    
       Document doc; // creates a new Document object that we will use to extract the html page and then extract the abstract text

       ArrayList<String> abstractList = new ArrayList<String>(); // creates a list that we will store our abstracts in


       try 
       {
         /*
           Our current searchstring uses the author's name + the name of an article from our searchFor list.
           This currently works for most cases since all test cases provide 1 search result
           If the search querery needs to be improved, the documentation below will help: 
           https://pubmed.ncbi.nlm.nih.gov/help/#citation-matcher-auto-search
         */

         String searchString = "";     

         for(int i = 1; i < searchFor.size(); i++)
         {
            try {

              searchString = searchFor.get(0) + " " + searchFor.get(i);

              /*
                Uses the searchString (author's name + title of article) to search PubMed using a heuristic search on PubMed
                This most likely fails, but since PubMed defaults to an auto search if the heuristic search fails
                It still allows us to find the article we are searching for
              */
              doc = Jsoup.connect("https://pubmed.ncbi.nlm.nih.gov/?term=" + searchString).get(); 
        
              // Selects the id "abstract" and look for the paragraph element of the first occurrence of the id abstract
              // In theory, this should not cause an issue, since only one HTML element is allowed to have the id abstract
              // Meaning it should be okay to only search for the first occurrence
              // More documentation: https://jsoup.org/apidocs/org/jsoup/nodes/Element.html#selectFirst(java.lang.String)
              // More documentation: https://jsoup.org/cookbook/extracting-data/selector-syntax

              Element abstractelement = doc.selectFirst("#abstract p"); 

              abstracttext = abstractelement.text(); // gets only the text of the abstract from the paragraph (<p>) HTML element
              // For more info: https://jsoup.org/apidocs/org/jsoup/nodes/Element.html#text(java.lang.String)

              abstractList.add(abstracttext);
            }
            catch (NullPointerException npe) {
               abstracttext = "no abstract";
               abstractList.add(abstracttext);
            }
            catch (MalformedURLException mue) {
               mue.printStackTrace();
               abstracttext = "error";
               abstractList.add(abstracttext);
            }
       }
     } catch (IOException e) {
        e.printStackTrace();
    }
            int count = 0;
            for (int k = 0; k < abstractList.size(); k++)
            {
               if (abstractList.get(k).equals("no abstract"))
               {
                  count++;
               }
            }
    System.out.println("Number of abstracts that did not have an abstract or failed to get an abstract: " + count);

    return abstractList;
    
    }

    public static ArrayList<String> Read_From_Excel() throws IOException{
       
       ArrayList<String> searchList = new ArrayList<String>();
       
       FileInputStream fins = new FileInputStream(new File("C:\\Users\\reyno\\Downloads\\Abstacts.xlsx"));

       XSSFWorkbook wb = new XSSFWorkbook(fins); // creates a workbook that we can search, which allows us to get the author's name and the titles of each publication
       
       /*
        Currently manually inputting the sheet index
        Starting at 2 which would be:
        "Bothwell, A Pub Abstracts"
       */

       XSSFSheet sheet = wb.getSheetAt(2);

       int rows = sheet.getLastRowNum(); // gets number of rows
       int cols = sheet.getRow(0).getLastCellNum(); // gets the number of columns

       XSSFRow row = sheet.getRow(0); // starting the row at 0 for sheet 2

       for (int i = 0; i < cols; i++)
       {
          XSSFCell cell = row.getCell(i);

          // tests if the cell is null, since testing the cell type would throw an error if null
          // This is only intended for the titles of each column in our target excel file, since we will not need data from any other column

          if (cell == null) 
             continue;
          if (cell.getCellType() == CellType.STRING)
          {
             String cellValue = cell.getStringCellValue(); // gets the value of the cell if it is a string value
             if (cellValue.toLowerCase().equals("researcher")) //if the value of the cell is equal to "researcher", then we get the name of that researcher
             {
                XSSFRow tempRow = sheet.getRow(1);
                XSSFCell tempCell = tempRow.getCell(i); //creating temp objects so we do not accidentally shift the row and cells, since we still need the titles
                cellValue = tempCell.getStringCellValue();
                searchList.add(cellValue);
             }
             if (cellValue.toLowerCase().equals("title"))
             {
                for (int j = 1; j <= rows; j++)
                {
                   row = sheet.getRow(j);
                   cell = row.getCell(i);
                   cellValue = cell.getStringCellValue(); // loops through each cell in the specified "title" column until we have all the titles in our list
                   searchList.add(cellValue);
                }
             }
          }
       }
       fins.close(); //closes the inputstream

       // the author's name will always be the first index followed by the titles
       return searchList;
    }

    public static void Write_To_Excel(ArrayList<String> writingList) throws Exception {
        try {

           FileInputStream fins = new FileInputStream(new File("C:\\Users\\reyno\\Downloads\\Abstacts.xlsx")); // misspelled abstracts

           XSSFWorkbook wb = new XSSFWorkbook(fins);

           // Currently manually inputting the sheet index
           // Starting at 2 which would be:
           // "Bothwell, A Pub Abstracts"
           XSSFSheet sheet = wb.getSheetAt(2);

           //int rows = sheet.getLastRowNum(); //gets the number of rows in the sheet

           // SocketTimeoutException causing it to not work in some cases
           // Using the size of the list allows us to still run the code
           // May need to rerun since this is often caused by connection issues
           int rows = writingList.size(); // SocketTimeoutException causing it to not work in some cases
           int cols = sheet.getRow(1).getLastCellNum(); //gets the number of columns in the sheet

           XSSFRow row = sheet.getRow(0);

           for(int i = 0; i < cols; i++)
           {
              XSSFCell cell = row.getCell(i);
              if (cell == null) // if the cell is null for whatever reason, it will throw an error when trying to get the cell type
                 continue;
              if (cell.getCellType() == CellType.STRING)
              {
                 String valueOfCell = cell.getStringCellValue();
                 if (valueOfCell.toLowerCase().equals("abstract"))
                 {
                    for (int j = 1; j <= rows; j++)
                    {
                       int abIndex = j - 1; // allows us to access the correct abstract in our list
                       row = sheet.getRow(j); // sets us on the right row 
                       row.createCell(i, CellType.STRING).setCellValue(writingList.get(abIndex)); // 10 is number of the column that contains "Abstract"
                       // we then "create" a cell which has a cell type of String, which allows us to write our abstract to the cell.
                    }
                 }
              }
           }
           FileOutputStream out = new FileOutputStream(new File("C:\\Users\\reyno\\Downloads\\abstract2.xlsx"));
           wb.write(out);
           out.close();
           fins.close();
       }
       catch (Exception e) {
          e.printStackTrace();
       }
       
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
            e.printStackTrace();
            return "failed to find filepath";
        }
    }
}
