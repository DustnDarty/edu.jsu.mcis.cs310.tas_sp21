/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis.cs310.tas_sp21;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.ArrayList;

/**
 *
 * @author dustn
 */
public class TASLogic {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){
        LocalTime unixEpoch = Instant.ofEpochMilli(0).atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime startTime = unixEpoch; //Initialize to 1/1/1970 00:00:00
        LocalTime endTime = unixEpoch; //Initialize to 1/1/1970 00:00:00
        int timeSum = 0;
        int numOfClockOuts = 0;
        
        for (Punch punch : dailypunchlist) {
            switch (punch.getPunchtypeid()) {
                case 1:
                    startTime = Instant.ofEpochMilli(punch.getAdjustedtimestamp()).atZone(ZoneId.systemDefault()).toLocalTime();
                    break;
                case 0:
                    if(!startTime.equals(unixEpoch)){
                        endTime = Instant.ofEpochMilli(punch.getAdjustedtimestamp()).atZone(ZoneId.systemDefault()).toLocalTime();
                        numOfClockOuts++;
                    }
                    break;
                default:
                    //Timed out
                    startTime = unixEpoch; //Reset to 1/1/1970 00:00:00
                    endTime = unixEpoch;
                    break;
            }
            if(!(startTime.equals(unixEpoch)) && !(endTime.equals(unixEpoch))){
                timeSum += MINUTES.between(startTime, endTime);
                startTime = unixEpoch;
                endTime = unixEpoch;
            }
        }
        if((numOfClockOuts < 2) && (timeSum > shift.getLunchDeduct())){
            timeSum -= shift.getLunchDuration();
        }
        return timeSum;
    }
}
