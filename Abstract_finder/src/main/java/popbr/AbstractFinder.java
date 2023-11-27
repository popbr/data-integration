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

import java.util.ArrayList;

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
       
       System.out.println("Welcome to Abstract/DOI Finder.");

       ArrayList<String> searchList = Read_From_Excel(); // will return a searchList that has the author's name and all of the titles for our search query
    
       ArrayList<ArrayList<String>> contentList = RetrieveData(searchList); //takes a few minutes to accomplish due to having to search on the Internet

       Write_To_Excel(contentList); // Currently only does one sheet at a time and needs to be manually updated

       System.out.println("Thanks for coming! Your abstracts and DOIs should be in your Excel file now");
       
       System.exit(0);
    }
    
    // make the new return type ArrayList<ArrayList<String>> to return two lists, so we can make this program a bit more efficient
    // need to figure out how to differentiate between what threw an error (we now are doing two things that can throw a NullPointerException 
    // and we don't want the error to crash the program because of the amount of entries we have --> right now, I'm just making it set the text for both to not having
    // one, even if it does have one, but not the other
    // one idea --> have a boolean variable for each to see how far it made it into the program
    public static ArrayList<ArrayList<String>> RetrieveData(ArrayList<String> searchFor)
    {
    
       String abstracttext = " "; // Will be overwritten by the abstract if we succeed.

       String doiText = " "; // Will be overwritten by the DOI if we succeed
    
       Document doc; // creates a new Document object that we will use to extract the html page and then extract the abstract text

       ArrayList<String> abstractList = new ArrayList<String>(); // creates a list that we will store our abstracts in

       ArrayList<String> doiList = new ArrayList<String>(); // creates a list to store the doi in

       ArrayList<ArrayList<String>> returnedList = new ArrayList<ArrayList<String>>();

       boolean hasAbstract = false, hasDOI = false; //declares and initializes them

       try 
       {
         /*
           Our current searchstring uses the author's name + the name of an article from our searchFor list.
           This currently works for most cases since all test cases provide 1 search result
           If the search query needs to be improved, the documentation below will help: 
           https://pubmed.ncbi.nlm.nih.gov/help/#citation-matcher-auto-search
         */

         String searchString = "";     

         for(int i = 1; i < searchFor.size(); i++)
         {
            try {

              hasAbstract = false; //resets them for each loop in case they got set to true
              hasDOI = false;

              searchString = searchFor.get(0) + " " + searchFor.get(i);

              /*
                Uses the searchString (author's name + title of article) to search PubMed using a heuristic search on PubMed
                This most likely fails, but since PubMed defaults to an auto search if the heuristic search fails
                It still allows us to find the article we are searching for
              */
              doc = Jsoup.connect("https://pubmed.ncbi.nlm.nih.gov/?term=" + java.net.URLEncoder.encode(searchString, "UTF-8")).get(); 
        
              // Selects the id "abstract" and look for the paragraph element of the first occurrence of the id abstract
              // In theory, this should not cause an issue, since only one HTML element is allowed to have the id abstract
              // Meaning it should be okay to only search for the first occurrence
              // More documentation: https://jsoup.org/apidocs/org/jsoup/nodes/Element.html#selectFirst(java.lang.String)
              // More documentation: https://jsoup.org/cookbook/extracting-data/selector-syntax

              Element abstractelement = doc.selectFirst("#abstract p");

              Element doiElement = doc.selectFirst("span.identifier.doi a");

              abstracttext = abstractelement.text(); // gets only the text of the abstract from the paragraph (<p>) HTML element
              // For more info: https://jsoup.org/apidocs/org/jsoup/nodes/Element.html#text(java.lang.String)

              abstractList.add(abstracttext);

              hasAbstract = true; //if we make it to this part, we will have an abstract (no exception thrown)

              doiText = doiElement.text();

              doiList.add(doiText);

              hasDOI = true; //if we make it to this part, we will have a DOI (no exception thrown)

            }
            catch (NullPointerException npe) { //need to implement the boolean checking 
               /*
               abstracttext = "no abstract on PubMed";
               abstractList.add(abstracttext);
               doiText = "no doi on PubMed";
               doiList.add(doiText);
               */
               if (Boolean.FALSE.equals(hasAbstract))
               {
                  abstracttext = "no abstract on PubMed";
                  abstractList.add(abstracttext);
                  try 
                  {
                     doiText = RetrieveDOI(searchString);
                  }
                  catch (Exception e)
                  {
                     e.printStackTrace();
                  }
                  doiList.add(doiText);
               }
               if (Boolean.FALSE.equals(hasDOI) && hasAbstract) // the abstractList will already have the abstract so you do not want to double add it to the list
               {
                  doiText = "no doi on PubMed";
                  doiList.add(doiText);
               }
            }
            catch (MalformedURLException mue) {
               mue.printStackTrace();
               abstracttext = "error";
               abstractList.add(abstracttext);
               doiText = "error";
               doiList.add(doiText);
            }
       }
     } catch (IOException e) {
        e.printStackTrace();
    }
            int count = 0, doiCount = 0;
            for (int k = 0; k < abstractList.size(); k++)
            {
               if (abstractList.get(k).equals("no abstract on PubMed"))
                  count++;
               if (doiList.get(k).equals("no doi on PubMed"))
                  doiCount++;
            }
    System.out.println("Number of publications that did not have an abstract on PubMed: " + count);
    System.out.println("Number of publications that did not have a DOI on PubMed: " + doiCount);

    returnedList.add(abstractList);
    returnedList.add(doiList);

    return returnedList;
    
    }

    public static ArrayList<String> Read_From_Excel() throws IOException, Exception{
       
       ArrayList<String> searchList = new ArrayList<String>();
       
       String BasePath = EstablishFilePath();
       String SourceFile = BasePath + File.separator + "target" + File.separator + "downloads" + File.separator + "Publication_Abstracts_Only_Dataset_9-26-23.xlsx";
       
       FileInputStream fins = new FileInputStream(new File(SourceFile));

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

    // old params: ArrayList<String> writingList, ArrayList<String> doiList
    // new param because of new modified method: ArrayList<ArrayList<String>>
    public static void Write_To_Excel(ArrayList<ArrayList<String>> writeList) throws Exception {
        try {
            String BasePath = EstablishFilePath();
            String AbstractFile = BasePath + File.separator + "target" + File.separator + "downloads" + File.separator + "Publication_Abstracts_Only_Dataset_9-26-23.xlsx";

           ArrayList<String> abstractList = writeList.get(0);
           ArrayList<String> doiList = writeList.get(1); 

           FileInputStream fins = new FileInputStream(new File(AbstractFile)); 

           XSSFWorkbook wb = new XSSFWorkbook(fins);

           // Currently manually inputting the sheet index
           // Starting at 2 which would be:
           // "Bothwell, A Pub Abstracts"
           XSSFSheet sheet = wb.getSheetAt(2);

           //int rows = sheet.getLastRowNum(); //gets the number of rows in the sheet

           // SocketTimeoutException causing it to not work in some cases
           // Using the size of the list allows us to still run the code
           // May need to rerun since this is often caused by connection issues
           int rows = abstractList.size(); // SocketTimeoutException causing it to not work in some cases
           int doiRows = doiList.size();
           int cols = sheet.getRow(0).getLastCellNum(); //gets the number of columns in the sheet

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
                       row.createCell(i, CellType.STRING).setCellValue(abstractList.get(abIndex));
                       // we then "create" a cell which has a cell type of String, which allows us to write our abstract to the cell.
                    }
                    row = sheet.getRow(0); //sets the row back to 0 after running
                 }
                 if (valueOfCell.toUpperCase().equals("DOI"))
                 {
                    for (int l = 1; l <= doiRows; l++)
                    {
                       int doiIndex = l - 1; // allows us to access the correct abstract for each row, since row would be 1 more than the actual index
                       row = sheet.getRow(l); // sets us on the correct row
                       row.createCell(i, CellType.STRING).setCellValue(doiList.get(doiIndex));
                    }
                    row = sheet.getRow(0); //sets the row back to 0 after running
                 }
              }
           }

           String AbstractFile2 = BasePath + File.separator + "target" + File.separator + "downloads" + File.separator + "Abstacts2.xlsx";

           FileOutputStream out = new FileOutputStream(new File(AbstractFile2));
           wb.write(out);
           out.close();
           fins.close();
       }
       catch (Exception e) {
          e.printStackTrace();
       }   
    }

    public static String RetrieveDOI(String search) throws Exception {
       String searchString = search;
       String doiText = " ";
       Document doc;
       try
       {
          doc = Jsoup.connect("https://pubmed.ncbi.nlm.nih.gov/?term=" + java.net.URLEncoder.encode(searchString, "UTF-8")).get();
          Element doiElement = doc.selectFirst("span.identifier.doi a");
          doiText = doiElement.text();
       }
       catch (NullPointerException npe) 
       {
          doiText = "no doi on PubMed";
          return doiText;
       }
       return doiText;
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
