/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.jsu.mcis;

import static edu.jsu.mcis.Cs310DatabaseJsonSp21.getJSONData;
import org.json.simple.JSONArray;

/**
 *
 * @author dustn
 */
public class Main {
     
        public static void main(String[] args) {
                // Convert DataBase to JSON; 
        
        System.out.println("CONVERSION RESULTS (DataBase to JSON)");
        System.out.println("================================");

        //String json = Converter.csvToJson(csvFileString);
        JSONArray json = getJSONData();
        System.out.println(json);
    }
}
