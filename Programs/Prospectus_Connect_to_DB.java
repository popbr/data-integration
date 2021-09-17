// code/java/Prospectus_Connect_To_DB.java

import java.sql.*;
import java.util.Scanner;  // Import the Scanner class

public class Prospectus_Connect_To_DB {
  public static void main(String[] args) {
    try (Connection conn = 
          DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB"
          + "?user=DBTestUser"
          + "&password=wali0e^23"
          + "&allowMultiQueries=true"
          + "&createDatabaseIfNotExist=true"
          + "&useSSL=true"); 
          Statement stmt = conn.createStatement(); ) 
    {
      Scanner myObj = new Scanner(System.in);  // Create a Scanner object
      String strSelect, Name, Website;
      int rowCount = 0;
     
      System.out.print("\nTest, Test\n");

      strSelect = "SELECT Name, Website FROM CompanyDB";
      PreparedStatement VACCINE_NetM = conn.prepareStatement(strSelect);
      ResultSet rset = VACCINE_NetM.executeQuery();  

      while (rset.next()) 
      {
        Name = rset.getString("Name");
        Website = rset.getString("Website");
        System.out.println("The " + Name + " Database, found at " + Website + ".");
        rowCount++;
      }

      System.out.print("\nB-B-B-Bennie and the Jets\n");
      conn.close();
    } 
    catch (SQLException ex) 
    {
      ex.printStackTrace();
    }
  }
}