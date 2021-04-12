
package edu.jsu.mcis.cs310.tas_sp21;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.*;

public class TASLogic {

    /**
     * @param args the command line arguments
     */
    
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
}
