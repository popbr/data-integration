import java.io.File;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Prospectus_Write_to_Excel 
{
  public static void main(String[] args) throws Exception
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
    ReschData.put("5", new Object[] { "Elton", "yes", "Student" });
  
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
}
