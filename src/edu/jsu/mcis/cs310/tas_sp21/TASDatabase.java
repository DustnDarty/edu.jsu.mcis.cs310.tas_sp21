package edu.jsu.mcis.cs310.tas_sp21;
import java.sql.*;
import java.time.LocalTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ArrayList;


public class TASDatabase {
    
        private Connection conn = null;
        private String query;
        private PreparedStatement pstSelect = null, pstUpdate = null;
        private ResultSet resultset = null;
        private boolean hasresults;
        private int updateCount;

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
            Shift outputShift;
            
            try{
               
                // Prepare select query
                query = "SELECT * FROM tas.shift WHERE id = " + id;
                pstSelect = conn.prepareStatement(query);
               
                // Execute select query
                hasresults = pstSelect.execute();
               
                while(hasresults || pstSelect.getUpdateCount() != -1 ){
                    if(hasresults){
                       
                        resultset = pstSelect.getResultSet();
                        resultset.next();
                       
                        String description = resultset.getString("description");
                        LocalTime start = LocalTime.parse(resultset.getString("start"));
                        LocalTime stop = LocalTime.parse(resultset.getString("stop"));
                        int interval = resultset.getInt("interval");
                        int graceperiod = resultset.getInt("graceperiod");
                        int dock = resultset.getInt("dock");
                        LocalTime lunchstart = LocalTime.parse(resultset.getString("lunchstart"));
                        LocalTime lunchstop = LocalTime.parse(resultset.getString("lunchstop"));
                        int lunchdeduct = resultset.getInt("lunchdeduct");
                       
                        outputShift = new Shift(id, description, start, stop, interval, graceperiod, dock, lunchstart, lunchstop, lunchdeduct);
                       
                        return outputShift;
                    }
                }
            }
            catch(SQLException e){System.out.println(e);}
            
            //Shouldn't be reached with a valid shift ID.
            return null;
	}
	
	public Shift getShift(Badge badge){
            try{
               
                // Prepare select query from employee table
                query = "SELECT * FROM tas.employee WHERE badgeid = \"" + badge.getId() + "\"";
                pstSelect = conn.prepareStatement(query);
               
                // Execute select query
                hasresults = pstSelect.execute();
               
                while(hasresults || pstSelect.getUpdateCount() != -1 ){
                    if(hasresults){
                       
                        resultset = pstSelect.getResultSet();
                        resultset.next();
                        
                        int shiftid = resultset.getInt("shiftid");
                        
                        return getShift(shiftid); 
                    }
                    
                }
                
            }
            catch(SQLException e){System.out.println(e);}
            
            //Shouldn't be reached with a valid badge.
            return null;
	}
        
        public int insertPunch(Punch p){

            // Extract punch data from the Punch object
            int terminalid = p.getTerminalid(); 
            String badgeid = p.getBadgeid();
            long originalTS = p.getOriginaltimestamp();
            int punchtypeid = p.getPunchtypeid();
        
            // Convert originalTS to a Timestamp string
            GregorianCalendar ots = new GregorianCalendar();
            ots.setTimeInMillis(originalTS);
            String originaltimestamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(ots.getTime());
        
            // Insert this data into the database as a new punch
            try {
                // Prepare Update Query
                
                query = "INSERT INTO tas.punch (terminalid, badgeid, originaltimestamp, punchtypeid) VALUES (?, ?, ?, ?)";
                pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pstUpdate.setInt(1, terminalid);
                pstUpdate.setString(2, badgeid);
                pstUpdate.setString(3, originaltimestamp);
                pstUpdate.setInt(4, punchtypeid);
                
                // Execute Update Query
                updateCount = pstUpdate.executeUpdate();
                
                // Get New Key
                if(updateCount > 0){
                    
                    resultset = pstUpdate.getGeneratedKeys();
                    
                    if (resultset.next()) {
                        
                        // Return the id of the new punch (assigned by the database) as an integer
                        
                        return resultset.getInt(1);
                        
                    }
                }
  
            }
            catch(SQLException e){ System.out.println(e); }

            // Not reached with a valid Punch object
            return -1; 
    }
        
        
        public ArrayList<Punch> getDailyPunchList(Badge badge, long ts) {
            
            /* Initialize variables for punches */
            Punch obj;                                                                  
            ArrayList<Punch> output = new ArrayList<>();
            String strbadge = badge.getId();

            /* Get search date for select query */
            GregorianCalendar gc = new GregorianCalendar();                             
            gc.setTimeInMillis(ts);                                                     
            String date = (new SimpleDateFormat("yyyy-MM-dd")).format(gc.getTime());    

            try{

                /* Prepare SQL query */
                query = "SELECT * FROM tas.punch WHERE badgeid = ? AND DATE(originaltimestamp) = ?";
                pstSelect = conn.prepareStatement(query);
                pstSelect.setString(1, strbadge);
                pstSelect.setString(2, date);
                
                /* Execute query */
                hasresults = pstSelect.execute();
                
               while(hasresults || pstSelect.getUpdateCount() != -1 ){
                    if(hasresults){
                        
                        resultset = pstSelect.getResultSet();
                  
                        while(resultset.next()){
                            
                            int terminalid = resultset.getInt("terminalid");
                            int punchtypeid = resultset.getInt("punchtypeid");
                            
                            obj = new Punch(badge, terminalid, punchtypeid);
                            obj.setId(resultset.getInt("id"));
                            obj.setOriginalTimeStamp(resultset.getTimestamp("originaltimestamp").getTime());
                            
                            output.add(obj);                             
                        }

                    } 
                    else {
                       updateCount = pstSelect.getUpdateCount();
                       if(updateCount == -1){
                           break;
                        }
                   }
                    hasresults = pstSelect.getMoreResults();   
                }
                
           }
            catch(SQLException e){ System.out.println(e); }
            
            return output;
        }
        
        public ArrayList<Punch> getPayPeriodPunchList(Badge badge, long timestamp){
           ArrayList<Punch> punches = new ArrayList<>();
           GregorianCalendar ts = new GregorianCalendar();
           ts.setTimeInMillis(timestamp);
           ts.set(Calendar.DAY_OF_WEEK, 1);
           ts.set(Calendar.HOUR_OF_DAY, 0);
           ts.set(Calendar.MINUTE, 0);
           ts.set(Calendar.SECOND, 0); 
           
           for(int i = 1; i <= 7; i++){
               ts.set(Calendar.DAY_OF_WEEK, i);
               punches.addAll(getDailyPunchList(badge, ts.getTimeInMillis()));
           }
           
           return punches;
        }
        
        public Absenteeism getAbsenteeism(String badgeid, long payperiod){
            Absenteeism outputAbsenteeism;
            
            // Convert payperiod to a Timestamp string
            GregorianCalendar ts = new GregorianCalendar();
            ts.setTimeInMillis(payperiod);
            ts.set(Calendar.DAY_OF_WEEK, 1);
            ts.set(Calendar.HOUR_OF_DAY, 0);
            ts.set(Calendar.MINUTE, 0);
            ts.set(Calendar.SECOND, 0); 
            String payperiodStr = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(ts.getTime());
            
            try{
                // Prepare select query
                query = "SELECT * FROM tas.absenteeism WHERE badgeid = ? AND payperiod = ?";
                pstSelect = conn.prepareStatement(query);
                pstSelect.setString(1, badgeid);
                pstSelect.setString(2, payperiodStr);
                
                // Execute select query
                hasresults = pstSelect.execute();
                
                while(hasresults || pstSelect.getUpdateCount() != -1 ){
                    if(hasresults){
                        resultset = pstSelect.getResultSet();
                        resultset.next();
                        
                        String absBadge = resultset.getString("badgeid");
                        long absPayperiod = resultset.getTimestamp("payperiod").getTime();
                        double absPercentage = resultset.getDouble("percentage");
                        
                        outputAbsenteeism = new Absenteeism(absBadge, absPayperiod, absPercentage);
                        
                        return outputAbsenteeism;
                    }
                }
            }
            catch(SQLException e){System.out.println(e);}
            
            //Shouldn't be reached with a valid badge string and payperiod timestamp.
            return null;
        }
        
        public void insertAbsenteeism(Absenteeism absenteeism){
            // Convert originalTS to a Timestamp string
            GregorianCalendar ots = new GregorianCalendar();
            ots.setTimeInMillis(absenteeism.getPayperiod());
            String payperiod = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(ots.getTime());
            
            try {
                // Prepare Update Query
                    
                query = "INSERT INTO tas.absenteeism (badgeid, payperiod, percentage) VALUES (?, ?, ?)";
                pstUpdate = conn.prepareStatement(query);
                pstUpdate.setString(1, absenteeism.getBadgeid());
                pstUpdate.setString(2, payperiod);
                pstUpdate.setDouble(3, absenteeism.getPercentage());
                    
                // Execute Update Query
                updateCount = pstUpdate.executeUpdate();
            }
            catch(SQLIntegrityConstraintViolationException e){
                try {
                    payperiod = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(ots.getTime());
                    // Prepare Update Query
                    
                    query = "UPDATE tas.absenteeism SET percentage = ? WHERE badgeid = ? AND payperiod = ?";
                    pstUpdate = conn.prepareStatement(query);
                    pstUpdate.setDouble(1, absenteeism.getPercentage());
                    pstUpdate.setString(2, absenteeism.getBadgeid());
                    pstUpdate.setString(3, payperiod);
                    
                    // Execute Update Query
                    updateCount = pstUpdate.executeUpdate();
                }
                catch(SQLException f){ System.out.println(f); }
            }
            catch(SQLException e){ System.out.println(e); }
        }
}
