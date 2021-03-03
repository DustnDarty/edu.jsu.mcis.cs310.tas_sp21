package edu.jsu.mcis.cs310.tas_sp21;
import java.sql.*;

public class TASDatabase {
    
        private Connection conn = null;
        private String query;
        private PreparedStatement pstSelect = null;
        private ResultSet resultset = null;
        private boolean hasresults;

	public TASDatabase(){
		try {
                    /* Identify the Server */
                    
                    String server = ("jdbc:mysql://localhost/tas");
                    String username = "tas1user";
                    String password = "CS310";
                    
                    /* Load the MySQL JDBC Driver */
            
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    
                    /* Open Connection */

                    conn = DriverManager.getConnection(server, username, password);
                    
                    if(!conn.isValid(0)){
                        throw new SQLException();
                    }
                }
                catch(SQLException e){ System.out.println("SQL Connection failed! Invalid database setup?"); }
                catch(ClassNotFoundException e){ System.out.println("JDBC driver not found, make sure MySQLDriver is added as a library!"); }
                catch (Exception e){}
	}
	
	public void close(){
            try {
                conn.close();
            }
            catch(SQLException e){}
            finally{
                if (resultset != null) { try { resultset.close(); resultset = null; } catch (SQLException e) {} }
                if (pstSelect != null) { try { pstSelect.close(); pstSelect = null; } catch (SQLException e) {} }
            }
	}
		
	public Punch getPunch(int id){ // method of the database class and provide the punch ID as a parameter. 
                //note: changed Punch to void for return type
            Punch outputPunch;
            
            try{
                
                /*Prepare Select Query*/
                query = "SELECT * FROM tas.punch WHERE id = " + id;
                pstSelect = conn.prepareStatement(query);
                
                
                /* Execute Select Query */
                hasresults = pstSelect.execute();
                
                
                while( hasresults || pstSelect.getUpdateCount() != -1 ){
                    if (hasresults) {
                        
                        resultset = pstSelect.getResultSet();
                        resultset.next();
                        
                        int terminalid = resultset.getInt("terminalid");
                        String badgeid = resultset.getString("badgeid");
                        long originaltimestamp = resultset.getTimestamp("originaltimestamp").getTime(); 
                        int punchtypeid = resultset.getInt("punchtypeid");
                        
                        outputPunch = new Punch(getBadge(badgeid), terminalid, punchtypeid);
                        outputPunch.setOriginalTimeStamp(originaltimestamp);

                        return outputPunch;
                        
                    }
                }   
            }
            catch(SQLException e){System.out.println(e);}
            
            //Shouldn't be reached with a valid punch id
            return null;
	}
	
	public Badge getBadge(String id){  // method of the database class and provide the badge ID as a parameter.
            Badge outputBadge;
            
            try {
                /* Prepare Select Query */
                
                query = "SELECT * from tas.badge where id = \"" + id + "\"";
                pstSelect = conn.prepareStatement(query);
                
                /* Execute Select Query */
                hasresults = pstSelect.execute();
                
                while ( hasresults || pstSelect.getUpdateCount() != -1 ) {
                    if ( hasresults ) {
                        resultset = pstSelect.getResultSet();
                        
                        resultset.next();                        
                        outputBadge = new Badge(resultset.getString("id"), resultset.getString("description"));
                        
                        return outputBadge;
                    }
                }
            }
            catch(SQLException e){ System.out.println("Error in getBadge()"); }
            
            //Shouldn't be reached with a valid badge ID.
            return null;
	}
	
	public Shift getShift(int id){ // method of the database class and provide the shift ID as a parameter.
            //Similarly, there should be " getBadge() " and " getShift() " methods for creating objects of the Badge and Shift classes.
            //see getPunch
            
            //Stubbed
            return null;
	}
	
	public Shift getShift(Badge badge){ // and one which accepts a Badge object as a parameter.
            /* This way, the user can retrieve a shift ruleset by its database ID, 
            or with the badge of an employee assigned to that shift.  
            See the attached unit test code for an example of what the completed class interfaces should look like.*/
            
            //Stubbed
            return null;
	}
}
