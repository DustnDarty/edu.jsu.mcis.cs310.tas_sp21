package edu.jsu.mcis.cs310.tas_sp21;

//import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class Punch {
    
    private int id; 
    private int terminalid;
    private String badgeid;
    private long originaltimestamp;
    private int punchtypeid;
    private String adjustmenttype;
    private long adjustedtimestamp;

    public Punch(Badge badge, int terminalid, int punchtypeid) {
        this.badgeid = badge.getId();
        this.terminalid = terminalid;
        this.originaltimestamp = System.currentTimeMillis();
        this.punchtypeid = punchtypeid;
        this.id = 0;
        this.adjustedtimestamp = 0; 
        this.adjustmenttype = null;
    }

    public int getId() {
        return id;
    }

    public int getTerminalid() {
        return terminalid;
    }

    public String getBadgeid() {
        return badgeid;
    }

    public long getOriginaltimestamp() {
        return originaltimestamp;
    }
    
    public long getAdjustedtimestamp() {
        return adjustedtimestamp;
    }

    public int getPunchtypeid() {
        return punchtypeid;
    }

    public String getAdjustmenttype() {
        return adjustmenttype;
    }
    
     public void setOriginalTimeStamp(long originaltimestamp) {
        this.originaltimestamp = originaltimestamp;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setAdjustmenttype(String adjustmenttype) {
        this.adjustmenttype = adjustmenttype;
    }
 
    public String printTimestamp(Boolean adjustedTimestamp){
        StringBuilder output = new StringBuilder();
        Date date;
        if(adjustedTimestamp){
            date = new Date(adjustedtimestamp);
        }
        else{
            date = new Date(originaltimestamp);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MM/dd/yyyy HH:mm:ss");
        String strDate = formatter.format(date);
        
        switch (punchtypeid) {
            case 1:
                output.append("#").append(badgeid).append(" ");
                output.append("CLOCKED IN: ");
                break;
            case 0:
                output.append("#").append(badgeid).append(" ");
                output.append("CLOCKED OUT: ");
                break;
            default:
                output.append("#").append(badgeid).append(" ");
                output.append("TIMED OUT: ");
                break;
        }
 
        output.append(strDate.toUpperCase());
        
        if(adjustedTimestamp){
            output.append(" ").append("(").append(adjustmenttype).append(")");
        }
            
        return output.toString();
     }
    
    public String printOriginalTimestamp(){
        return printTimestamp(false);
    }
    
    public String printAdjustedTimestamp(){
        return printTimestamp(true);
    }
  
    public void adjust(Shift s){
        
        Date date = new Date(originaltimestamp);
        
        // Get name of day
        SimpleDateFormat formatter = new SimpleDateFormat("EEE");
        String strDate = formatter.format(date).toUpperCase();
        
        //convert originaltimestamp from long to LocalTime object
        LocalTime lt = Instant.ofEpochMilli(originaltimestamp).atZone(ZoneId.systemDefault()).toLocalTime();
        
        switch(this.punchtypeid){
            /************************* PUNCH OUT *************************/
            case 0: 
                // On Weekdays
                if(!"SAT".equals(strDate) && !"SUN".equals(strDate) ){
                    //LATE CLOCK-OUT: SHIFT STOP within 15-min interval (ADJUST LATE CLOCK-OUT BACKWARD TO THE SCHEDULED END OF THE SHIFT)
                    if ( lt.isAfter(s.getStop()) && (Math.abs(s.getStop().toSecondOfDay() - lt.toSecondOfDay()) <= s.getInterval() * 60)){
                        adjustedtimestamp = originaltimestamp - 1000 * Math.abs(lt.toSecondOfDay() - (s.getStop().toSecondOfDay())); 
                        adjustmenttype = "Shift Stop";
                   }
                    // LATE LUNCH START (clock OUT punch WITHIN the lunch break is realigned with the START of lunch break
                    else if (lt.isAfter(s.getLunchStart())  &&  lt.isBefore(s.getLunchStop())){
                        adjustedtimestamp = originaltimestamp - 1000 * Math.abs(lt.toSecondOfDay() - s.getLunchStart().toSecondOfDay()); 
                        adjustmenttype = "Lunch Start";
                    }  
                    // EARLY CLOCK-OUT - GRACE PERIOD - 5 min (EARLY CLOCK OUT punch within GRACE PERIOD before END of shift is realigned with the STOPPING time of the shift)
                    else if (lt.isBefore(s.getStop()) && lt.isAfter(s.getLunchStop()) && (Math.abs(s.getStop().toSecondOfDay() - lt.toSecondOfDay()))  <= s.getGracePeriod() * 60){
                        adjustedtimestamp = originaltimestamp + 1000 * Math.abs(s.getStop().toSecondOfDay() - lt.toSecondOfDay()); 
                        adjustmenttype = "Shift Stop";
                    }
                    // DOCK 15 min (CLOCK OUT too early to fall within grace period; adjust punch BACKWARD in time from end of shift
                    else if( lt.isBefore(s.getStop()) && lt.isAfter(s.getLunchStop()) && (Math.abs(s.getStop().toSecondOfDay() - lt.toSecondOfDay())) > s.getGracePeriod() * 60 && (Math.abs(s.getStop().toSecondOfDay() - lt.toSecondOfDay()) <= s.getDock() * 60) )
                    {
                        adjustedtimestamp = (originaltimestamp + (Math.abs(lt.toSecondOfDay() - s.getStop().toSecondOfDay())) * 1000) - (s.getDock() * 60 * 1000); 
                        adjustmenttype = "Shift Dock"; 
                    }
                    // none: (if punch occurred at even time interval, no adjustment needed; reset seconds to zero
                    else if ((lt.getMinute() % s.getInterval()) == 0 ){
                        adjustedtimestamp = originaltimestamp - (lt.getSecond() * 1000);
                        adjustmenttype = "None";
                    } 
                    // INTERVAL ROUNDING applies only if OUTSIDE any of the Shift Rules 
                    else{
                     intervalRound(lt, s);
                    }
                }
                // WEEKEND REQUIRES ONLY INTERVAL ROUNDING
                else if ( "SAT".equals(strDate) || "SUN".equals(strDate) ){
                     intervalRound(lt, s);
                }
                break;
             
            /************************* PUNCH IN *************************/
            case 1: 
                // On Weekdays
                if( !"SAT".equals(strDate) && !"SUN".equals(strDate) ){
                     // EARLY CLOCK IN - SHIFT START -  15 min interval (adjust EARLY clock-in FORWARD)
                    if ( lt.isBefore(s.getStart()) && (Math.abs(s.getStart().toSecondOfDay() - lt.toSecondOfDay())  <= s.getInterval() * 60)){
                       adjustedtimestamp = originaltimestamp + 1000 * Math.abs(s.getStart().toSecondOfDay() - lt.toSecondOfDay());
                       adjustmenttype = "Shift Start";
                    }
                    // LUNCH STOP (the CLOCK IN punch WITHIN the lunch break realigns with the STOP of lunch break
                    else if (lt.isAfter(s.getLunchStart())  &&  lt.isBefore(s.getLunchStop())){
                       adjustedtimestamp = originaltimestamp + 1000 * Math.abs(s.getLunchStop().toSecondOfDay() - lt.toSecondOfDay()); 
                       adjustmenttype = "Lunch Stop";
                    }  
                    // LATE CLOCK IN - GRACE PERIOD - 5 min (LATE CLOCK IN within grace period AFTER start of shift realigns with STARTING time of the shift)
                    else if ( lt.isAfter(s.getStart()) && lt.isBefore(s.getLunchStart()) && ((Math.abs( lt.toSecondOfDay() - s.getStart().toSecondOfDay())  <= s.getGracePeriod() * 60))){
                       adjustedtimestamp = originaltimestamp - 1000 * Math.abs(lt.toSecondOfDay() - s.getStart().toSecondOfDay()); 
                       adjustmenttype = "Shift Start";
                    }
                    // DOCK 15 min (CLOCK IN too late to fall within grace period; adjust punch FORWARD in time from start of shift)
                    else if((lt.isAfter(s.getStart())) && (lt.isBefore(s.getLunchStart())) && (Math.abs(lt.toSecondOfDay() - s.getStart().toSecondOfDay())) > s.getGracePeriod() * 60 && (Math.abs(s.getStart().toSecondOfDay() - lt.toSecondOfDay()) <= s.getDock() * 60)){
                        adjustedtimestamp = (originaltimestamp - (Math.abs(lt.toSecondOfDay() - s.getStart().toSecondOfDay())) * 1000) + (s.getDock() * 60 * 1000);
                        adjustmenttype = "Shift Dock";
                    }
                    // none: (if punch occurred at even time interval, no adjustment needed; reset seconds to zero
                    else if ((lt.getMinute() % s.getInterval()) == 0 ){
                        adjustedtimestamp = originaltimestamp - (lt.getSecond() * 1000);
                        adjustmenttype = "None";
                    } 
                    // INTERVAL ROUNDING applies only if OUTSIDE any of the Shift Rules 
                    else{
                     intervalRound(lt, s);
                    }            
                }                
                // WEEKEND REQUIRES ONLY INTERVAL ROUNDING
                else if( "SAT".equals(strDate) || "SUN".equals(strDate) ){
                     intervalRound(lt, s);
                }              
                break;                
            /************************* TIME OUT *************************/
            default: 
                // Not implemented, see feature 4 description
                break;
        }                
    }
     
    //helper method for INTERVAL ROUNDING
    private void intervalRound(LocalTime lt, Shift s){ 
        /* Round up or down to nearest increment of the interval */
        long mod =  lt.getMinute() % s.getInterval();
        int halfInterval = s.getInterval()/2;
        if (mod != 0){
            if (mod < halfInterval ){ // round DOWN 
                adjustedtimestamp = originaltimestamp  - (mod*60*1000);
            }
            else if (mod >= halfInterval ){ // round UP 
                adjustedtimestamp = originaltimestamp  + ((s.getInterval() - mod)*60*1000);
            }
            adjustedtimestamp = adjustedtimestamp - (lt.getSecond() * 1000);   // adjust seconds to zero            
            adjustmenttype = "Interval Round";   
        }
    }
}
