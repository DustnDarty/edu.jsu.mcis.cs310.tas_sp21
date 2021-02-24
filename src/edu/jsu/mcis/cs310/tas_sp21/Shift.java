package edu.jsu.mcis.cs310.tas_sp21;
import java.time.LocalTime;
import static java.time.temporal.ChronoUnit.MINUTES;

public class Shift {
    private int id;
    private String description;
    private LocalTime start;
    private LocalTime stop;
    private int interval;
    private int gracePeriod;
    private int dock;
    private LocalTime lunchStart;
    private LocalTime lunchStop;
    private int lunchDeduct;
    private long lunchDuration; // length of lunch break
    private long shiftDuration; // length of shift

    public Shift(int id, String description, LocalTime start, LocalTime stop, int interval, int gracePeriod, int dock, LocalTime lunchStart, LocalTime lunchStop, int lunchDeduct) {
        this.id = id;
        this.description = description;
        this.start = start; // Start time of employee shift
        this.stop = stop; // stop time of employee shift
        this.interval = interval; // # of minutes before start of shift and after end of shift
        this.gracePeriod = gracePeriod; // # of minutes after start of shift and before end of shift used for "forgiveness"
        this.dock = dock; // amount of time adjusted to punish late clock in
        this.lunchStart = lunchStart; // 
        this.lunchStop = lunchStop;
        this.lunchDeduct = lunchDeduct;
        this.lunchDuration = MINUTES.between(lunchStart, lunchStop);
        this.shiftDuration = MINUTES.between(start, stop);
        
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getStop() {
        return stop;
    }

    public int getInterval() {
        return interval;
    }

    public int getGracePeriod() {
        return gracePeriod;
    }

    public int getDock() {
        return dock;
    }

    public LocalTime getLunchStart() {
        return lunchStart;
    }

    public LocalTime getLunchStop() {
        return lunchStop;
    }

    public int getLunchDeduct() {
        return lunchDeduct;
    }
    
    public long getLunchDuration(){
        return lunchDuration;
    }
    
    public long getShiftDuration(){
        return shiftDuration;
    }
    
    @Override
    public String toString() {
        
        //REFERENCE: Shift ID: description: start time - stop time, 
        //EXAMPLE1: "Shift 1: 07:00 - 15:30 (510 minutes); Lunch: 12:00 - 12:30 (30 minutes)"
        //EXAMPLE2: "Shift Early Lunch: 07:00 - 15:30 (510 minutes); Lunch: 11:30 - 12:00 (30 minutes)"
        StringBuilder s = new StringBuilder();
        
        s.append("Shift ").append(id).append(": ").append(description).append(": ").append(start).append(" - ");
        s.append(stop).append(" (").append(shiftDuration).append(" minutes); Lunch: ").append(lunchStart).append(" - ");
        s.append(lunchStop).append(" (").append(lunchDuration).append(" minutes)");
        
        return s.toString();
    }
}
