package de.ehealth.project.letitrip_beta.model.weather;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eHealth on 10.12.2015.
 */
public class DescriptionMapping {
    private static Map<String, String> descriptionMap;

    public static Map<String, String> getMap(){
        if(descriptionMap == null){
            descriptionMap = new HashMap<String, String>();
            descriptionMap.put("tornado", "Tornado");
            descriptionMap.put("tropical storm", "Tropensturm");
            descriptionMap.put("hurricane", "Hurrikan");
            descriptionMap.put("severe thunderstorms", "schwere Gewitter");
            descriptionMap.put("thunderstorms", "Gewitter");
            descriptionMap.put("mixed rain and snow", "Misch regen und Schnee");
            descriptionMap.put("mixed rain and sleet", "Misch regen und Graupel");
            descriptionMap.put("mixed snow and sleet", "Misch Schnee und Schneeregen");
            descriptionMap.put("freezing drizzle", "frierende Nässe");
            descriptionMap.put("drizzle", "Nieselregen");
            descriptionMap.put("freezing rain", "gefrierender Regen");
            descriptionMap.put("showers", "Duschen");
            descriptionMap.put("snow flurries", "Schneetreiben");
            descriptionMap.put("light snow showers", "Leichte Schneeschauer");
            descriptionMap.put("blowing snow", "Schneewehen");
            descriptionMap.put("snow", "Schnee");
            descriptionMap.put("hail", "Hagel");
            descriptionMap.put("sleet", "Schneeregen");
            descriptionMap.put("dust", "Staub");
            descriptionMap.put("foggy", "neblig");
            descriptionMap.put("haze", "Dunst");
            descriptionMap.put("smoky", "rauchig");
            descriptionMap.put("blustery", "stürmisch");
            descriptionMap.put("windy", "windig");
            descriptionMap.put("cold", "kalt");
            descriptionMap.put("cloudy", "bewölkt");
            descriptionMap.put("mostly cloudy (night)", "meistens bewölkt (Nacht)");
            descriptionMap.put("mostly cloudy (day)", "meistens bewölkt (Tag)");
            descriptionMap.put("partly cloudy (night)", "wechselnd bewölkt (Nacht)");
            descriptionMap.put("partly cloudy (day)", "wechselnd bewölkt (Tag)");
            descriptionMap.put("clear (night)", "klar ( Nacht)");
            descriptionMap.put("sunny", "sonnig");
            descriptionMap.put("fair (night)", "Fair (Nacht)");
            descriptionMap.put("fair (day)", "fair (day)");
            descriptionMap.put("mixed rain and hail", "Misch regen und Hagel");
            descriptionMap.put("hot", "heiß");
            descriptionMap.put("isolated thunderstorms", "Vereinzelte Gewitter");
            descriptionMap.put("scattered thunderstorms", "vereinzelte Gewitter");
            descriptionMap.put("scattered thunderstorms", "vereinzelte Gewitter");
            descriptionMap.put("scattered showers", "Vereinzelte Regenschauer");
            descriptionMap.put("heavy snow", "starker Schneefall");
            descriptionMap.put("scattered snow showers", "verstreut Schneeschauer");
            descriptionMap.put("heavy snow", "starker Schneefall");
            descriptionMap.put("partly cloudy", "Teilweise wolkig");
            descriptionMap.put("thundershowers", "Gewitter");
            descriptionMap.put("snow showers", "Schneeschauer");
            descriptionMap.put("isolated thundershowers", "isoliert Gewitter");
        }
        return descriptionMap;
    }
}
