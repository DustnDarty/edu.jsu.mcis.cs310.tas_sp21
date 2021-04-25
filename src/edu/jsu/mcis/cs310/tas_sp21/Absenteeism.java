
package edu.jsu.mcis.cs310.tas_sp21;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 *
 * @author Tiffany
 */
public class Absenteeism {
    
    // instance fields
    private String badgeid;
    private long payperiod;
    private double percentage;
    

    // constructor
    public Absenteeism(String badgeid, long payperiod, double percentage) {
        this.badgeid = badgeid;
        this.payperiod = firstDayOf(payperiod);
        this.percentage = percentage; 
    }

    // helper method to initialize payperiod
    private long firstDayOf(long payperiod){
        // INITIALIZE PAY PERIOD TO SUN 00:00:00
        GregorianCalendar ts = new GregorianCalendar();
        ts.setTimeInMillis(payperiod);
        ts.set(Calendar.DAY_OF_WEEK, 1);
        ts.set(Calendar.HOUR_OF_DAY, 0);
        ts.set(Calendar.MINUTE, 0);
        ts.set(Calendar.SECOND, 0); 
        return ts.getTimeInMillis();
    }
    
    // getter methods

    public String getBadgeid() {
        return badgeid;
    }

    public long getPayperiod() {
        return payperiod;
    }

    public double getPercentage() {
        return percentage;
    }

    // setter methods
    
    public void setBadgeid(String badgeid) {
        this.badgeid = badgeid;
    }

    public void setPayperiod(long payperiod) {
        this.payperiod = payperiod;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

       
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        
        Date date = new Date (this.payperiod);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        String startOfPayperiod = formatter.format(date);
        
        // Example output: "#28DC3FB8 (Pay Period Starting 09-02-2018): 2.50%"
        s.append("#").append(this.badgeid).append(" (Pay Period Starting ").append(startOfPayperiod).append("): ");
        s.append(String.format("%.2f%%", this.percentage));
        
        return s.toString();
    }  
    
}
