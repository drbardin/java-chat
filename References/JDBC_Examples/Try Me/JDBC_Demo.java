//Section 0. Import Java.sql package
import java.sql.*;

public class JDBC_Demo {

public static void main (String [] args) {

   // ***************************************************************
   // A. Report ids, and the # of them.
   // ***************************************************************

   // Section 1: Load the driver 
   try {   
      // Load the driver (registers itself)
      Class.forName ("com.mysql.jdbc.Driver");
      } 
   catch (Exception E) {
         System.err.println ("Unable to load driver.");
         E.printStackTrace ();
   } 

   try { 

      // Section 2. Connect to the databse
      Connection conn1; // An object of type connection 
      String dbUrl = "jdbc:mysql://104.236.206.121:3306/chat";
      String user = "root";
      String password = "362team1";
      conn1 = DriverManager.getConnection (dbUrl, user, password);
      System.out.println ("*** Connected to the database ***"); 

      // Section 3A. Create stmt1 object conn1
      Statement stmt1 = conn1.createStatement ();

      // Section 4A. Execute a query, receive result in a result set 
      ResultSet rs1 = stmt1.executeQuery ("select *" +
                                          "from TempTestTable"); 

      // Section 5A. Process the result set 

      //Print a header for the report:
      System.out.println ( );		
      System.out.println ("tempId");
      System.out.println ("------");		

      //Print report:

      int count = 0;

      int jId; // To store value of tempId attribute 

      while(rs1.next()) {
         // Access and print contents of one tuple
         jId = rs1.getInt ("tempId"); // Access by attribute Name
         System.out.println (jId);
         count = count + 1; 
      }			

      // Print # of TempTestTable ids: 
      System.out.println ( ); 		
      System.out.println ("Current number of ids in TempTestTable: " + count);
      System.out.println();
      // Section 6A. Close statement 
      stmt1.close (); 

   // ***************************
   // B. Using Prepared Statement  
   // ***************************

      // Section 3B. Create a Prepared Statement object 
      PreparedStatement stmt2 = conn1.prepareStatement ("INSERT INTO TempTestTable VALUES (NULL)"); 

      // Section 4B(i) Execute stmt2;
      stmt2.executeUpdate();											  	                          
      System.out.println("Added new row to TempTestTable.");
      System.out.println("New # of ids: "+(count+1));									  	                          

      // Section 6B. Close statement 
      stmt2.close (); 

      // Section 7. close connection
      
      conn1.close (); 

   } // End of try

   catch (SQLException E) {
      System.out.println ("SQLException: " + E.getMessage());
      System.out.println ("SQLState: " + E.getSQLState());
      System.out.println ("VendorError: " + E.getErrorCode());

   } // End of catch

} // end of main

} //end of class DemoJDBC

