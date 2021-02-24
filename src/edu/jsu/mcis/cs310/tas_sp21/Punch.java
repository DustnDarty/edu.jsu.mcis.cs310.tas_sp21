package edu.jsu.mcis.cs310.tas_sp21;

import java.util.GregorianCalendar;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setAdjustmenttype(String adjustmenttype) {
        this.adjustmenttype = adjustmenttype;
    }
    
    
    
    
    public String printOriginalTimestamp(){
        StringBuilder output = new StringBuilder();
        GregorianCalendar time = new GregorianCalendar();
        time.setTimeInMillis(originaltimestamp);
        
        if(punchtypeid==1){
            
            output.append("#").append(badgeid).append(" ");
            output.append("CLOCKED IN: ");
        }
        else if(punchtypeid==0)
        {
            
            output.append("#").append(badgeid).append(" ");
            output.append("CLOCKED OUT: ");
        }
        
        else{
           
            output.append("#").append(badgeid).append(" ");
            output.append("TIMED OUT: ");
        }
        
        String day_of_week[] = {"SUN", "MON", "TUE","WED", "THU", "FRI", "SAT"};
        
        //REVIEW ONCE TAS DATABASE IS IMPLEMENTED
        output.append(day_of_week[time.get(GregorianCalendar.DAY_OF_WEEK)]).append(" ");
        output.append((time.get(GregorianCalendar.MONTH)+1)).append("/");
        output.append((time.get(GregorianCalendar.DAY_OF_MONTH))).append("/");
        output.append((time.get(GregorianCalendar.YEAR))).append(" ");
        output.append((time.get(GregorianCalendar.HOUR_OF_DAY))).append(":");       
        output.append((time.get(GregorianCalendar.MINUTE))).append(":");
        output.append((time.get(GregorianCalendar.SECOND)));
        
        return output.toString();
 
    }
  
}
