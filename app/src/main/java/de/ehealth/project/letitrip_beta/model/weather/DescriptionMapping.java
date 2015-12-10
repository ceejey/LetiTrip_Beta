package de.ehealth.project.letitrip_beta.model.weather;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eHealth on 10.12.2015.
 */
public class DescriptionMapping {
    private static Map<String, String> descriptionMap;

    //TODO: Alle möglichen Descriptions anschauen
    public static Map<String, String> getMap(){
        if(descriptionMap == null){
            descriptionMap = new HashMap<String, String>();
            descriptionMap.put("Partly Cloudy", "Leicht bewölkt");
        }
        return descriptionMap;
    }
}
