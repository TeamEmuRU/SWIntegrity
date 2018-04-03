//This is an example of java program that accesses a database
//Meant for use as a test file, not for programming
//This file should return false, it is an example of good sql programming
import java.sql.*

String custname = request.getParameter("customerName"); // This should REALLY be validated too
// perform input validation to detect attacks
String query = "SELECT account_balance FROM user_data WHERE user_name = ? ";
 
PreparedStatement pstmt = connection.prepareStatement( query );
pstmt.setString( 1, custname); 
ResultSet results = pstmt.executeQuery( )
