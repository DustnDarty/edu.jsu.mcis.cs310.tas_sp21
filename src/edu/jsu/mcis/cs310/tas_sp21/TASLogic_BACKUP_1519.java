
package edu.jsu.mcis.cs310.tas_sp21;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.*;

<<<<<<< HEAD
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import static java.time.temporal.ChronoUnit.MINUTES;
import java.util.ArrayList;

/**
 *
 * @author dustn
 */
=======
>>>>>>> feature_5
public class TASLogic {

    /**
     * @param args the command line arguments
     */
<<<<<<< HEAD
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
=======
    
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist){
		ArrayList<HashMap<String, String>> jsonData = new ArrayList();
		
		for(Punch dailypunch : dailypunchlist){
			HashMap<String, String> punchData = new HashMap<>();
			punchData.put("id", String.valueOf(dailypunch.getId()));
			punchData.put("badgeid", String.valueOf(dailypunch.getBadgeid()));
			punchData.put("terminalid", String.valueOf(dailypunch.getTerminalid()));
			punchData.put("punchtypeid", String.valueOf(dailypunch.getPunchtypeid()));
			punchData.put("punchdata", String.valueOf(dailypunch.getAdjustmenttype())); //may have to set this one up in punch, looks like he uses adjustedtimestamp
			punchData.put("originaltimestamp", String.valueOf(dailypunch.getOriginaltimestamp()));
			punchData.put("adjustedtimestamp", String.valueOf(dailypunch.getAdjustedTimestamp()));			
			jsonData.add(punchData);
		}
		String json = JSONValue.toJSONString(jsonData);
		return json;
	}
>>>>>>> feature_5
}
