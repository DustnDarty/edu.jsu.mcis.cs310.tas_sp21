package edu.jsu.mcis.cs310.tas_sp21;

//import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.time.LocalTime;

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
    
    
 
    public String printOriginalTimestamp(){
        StringBuilder output = new StringBuilder();
        Date date = new Date(originaltimestamp);

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
            
        return output.toString();
     }
  
    public void adjust(Shift s){
        
        Date date = new Date(originaltimestamp);

        SimpleDateFormat formatter = new SimpleDateFormat("EEE");
        String strDate = formatter.format(date);
        
        switch(this.punchtypeid){
            case 0: //punch out
                //insert code
                //shift stop
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getStop().toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getStop().plusMinutes(s.getInterval()).toSecondOfDay() * 1000 ){
                    adjustedtimestamp = s.getStop().toSecondOfDay() * 1000;
                    adjustmenttype = "Shift Stop";
                    }
                }
                
                //lunch start
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getLunchStart().toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getLunchStop().toSecondOfDay() * 1000 ){
                    adjustedtimestamp = s.getLunchStart().toSecondOfDay() * 1000;
                    adjustmenttype = "Lunch Start";
                    }   
                }
                 
                
                //interval
                if( strDate == "SAT" || strDate == "SUN" )
                if( s.getStop().plusMinutes(s.getInterval()).toSecondOfDay() * 1000 < originaltimestamp ){
                    if( s.getStop().plusMinutes(s.getGracePeriod()).toSecondOfDay() * 1000 < originaltimestamp && s.getStop().plusMinutes(s.getDock()).toSecondOfDay() * 1000 < originaltimestamp ){
                        if( originaltimestamp < s.getLunchStart().toSecondOfDay() * 1000 && s.getLunchStop().toSecondOfDay() * 1000 < originaltimestamp ){
                            if( s.getStop().plusMinutes(s.getInterval()).toSecondOfDay() * 1000 < originaltimestamp){
                                adjustedtimestamp = originaltimestamp;
                            }
                            adjustmenttype = "Interval Round";
                        }
                    }       // UNFINISHED
                }
                
                // graceperiod
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getStop().minusMinutes(s.getGracePeriod()).toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getStop().toSecondOfDay() * 1000 ){
                    adjustedtimestamp = s.getStop().toSecondOfDay() * 1000;
                    adjustmenttype = "Grace Period";
                    }
                }
                
                // dock
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getStop().minusMinutes(s.getDock()).toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getStop().minusMinutes(s.getGracePeriod()).toSecondOfDay() * 1000 ){
                    adjustedtimestamp = originaltimestamp - (s.getDock() * 60000);
                    adjustmenttype = "Dock";
                    }
                }
                
                // none
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getStop().minusMinutes(1).toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getStop().plusMinutes(1).toSecondOfDay() * 1000  ){
                    adjustedtimestamp = s.getStop().toSecondOfDay() * 1000;
                    adjustmenttype = "None";
                    }
                }
                break;
            
            case 1: // punch in
                // insert code
                // shift start
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getStart().minusMinutes(s.getInterval()).toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getStart().toSecondOfDay() * 1000 ){
                    adjustedtimestamp = s.getStart().toSecondOfDay() * 1000;
                    adjustmenttype = "Shift Start";
                    }
                }
                
                // lunch stop
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getLunchStart().toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getLunchStop().toSecondOfDay() * 1000 ){
                    adjustedtimestamp = s.getStop().toSecondOfDay() * 1000;
                    adjustmenttype = "Lunch Stop";
                    }
                }
                
                // interval
                if( strDate == "SAT" || strDate == "SUN" ){
                    if( originaltimestamp < s.getStart().minusMinutes(s.getInterval()).toSecondOfDay() * 1000 ){
                        if( originaltimestamp <  s.getStart().minusMinutes(s.getGracePeriod()).toSecondOfDay() * 1000  && originaltimestamp < s.getStart().minusMinutes(s.getDock()).toSecondOfDay() * 1000 ){
                            if( originaltimestamp < s.getLunchStart().toSecondOfDay() * 1000 && s.getLunchStop().toSecondOfDay() * 1000 < originaltimestamp ){
                            adjustedtimestamp = originaltimestamp;
                            adjustmenttype = "Interval Round";
                            }
                        }       // UNFINISHED
                    }
                }
                
                // grace period
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getStart().toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getStart().plusMinutes(s.getGracePeriod()).toSecondOfDay() * 1000 ){
                    adjustedtimestamp = s.getStart().toSecondOfDay() * 1000;
                    adjustmenttype = "Grace Period";
                    }
                }
                
                // dock
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getStart().plusMinutes(s.getGracePeriod()).toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getStart().plusMinutes(s.getDock()).toSecondOfDay() * 1000 ){
                    adjustedtimestamp = originaltimestamp + (s.getDock() * 60000);
                    adjustmenttype = "Dock";
                    }
                }
                
                // none
                if( strDate != "SAT" && strDate != "SUN" ){
                    if( s.getStart().minusMinutes(1).toSecondOfDay() * 1000 < originaltimestamp && originaltimestamp < s.getStart().plusMinutes(1).toSecondOfDay() * 1000 ){
                    adjustedtimestamp = s.getStart().toSecondOfDay() * 1000;
                    adjustmenttype = "None";
                    }
                }
                break;
                
        }
    }
    
    public String printAdjustedTimestamp(){
        StringBuilder output = new StringBuilder();
        Date date = new Date(originaltimestamp);

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
 
        output.append(strDate.toUpperCase()).append("(").append(adjustmenttype).append(")");
            
        return output.toString();
    }
   
}
