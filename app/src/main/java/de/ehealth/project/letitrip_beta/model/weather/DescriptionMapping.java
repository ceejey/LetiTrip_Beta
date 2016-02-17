package de.ehealth.project.letitrip_beta.model.weather;

import java.util.HashMap;
import java.util.Map;

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
            descriptionMap.put("freezing drizzle", "Frierende Nässe");
            descriptionMap.put("drizzle", "Nieselregen");
            descriptionMap.put("freezing rain", "Gefrierender Regen");
            descriptionMap.put("showers", "Duschen");
            descriptionMap.put("snow flurries", "Schneetreiben");
            descriptionMap.put("light snow showers", "Leichte Schneeschauer");
            descriptionMap.put("blowing snow", "Schneewehen");
            descriptionMap.put("snow", "Schnee");
            descriptionMap.put("hail", "Hagel");
            descriptionMap.put("sleet", "Schneeregen");
            descriptionMap.put("dust", "Staub");
            descriptionMap.put("foggy", "Neblig");
            descriptionMap.put("haze", "Dunst");
            descriptionMap.put("smoky", "Rauchig");
            descriptionMap.put("blustery", "Stürmisch");
            descriptionMap.put("windy", "Windig");
            descriptionMap.put("cold", "Kalt");
            descriptionMap.put("cloudy", "Bewölkt");
            descriptionMap.put("mostly cloudy (night)", "Meistens bewölkt (Nacht)");
            descriptionMap.put("mostly cloudy (day)", "Meistens bewölkt (Tag)");
            descriptionMap.put("partly cloudy (night)", "Wechselnd bewölkt (Nacht)");
            descriptionMap.put("partly cloudy (day)", "Wechselnd bewölkt (Tag)");
            descriptionMap.put("clear (night)", "Klar (Nacht)");
            descriptionMap.put("sunny", "Sonnig");
            descriptionMap.put("fair (night)", "Fair (Nacht)");
            descriptionMap.put("fair (day)", "Fair (Tag)");
            descriptionMap.put("mixed rain and hail", "Misch regen und Hagel");
            descriptionMap.put("hot", "Heiß");
            descriptionMap.put("isolated thunderstorms", "Vereinzelte Gewitter");
            descriptionMap.put("scattered thunderstorms", "Vereinzelte Gewitter");
            descriptionMap.put("scattered thunderstorms", "Vereinzelte Gewitter");
            descriptionMap.put("scattered showers", "Vereinzelte Regenschauer");
            descriptionMap.put("heavy snow", "Starker Schneefall");
            descriptionMap.put("scattered snow showers", "Verstreut Schneeschauer");
            descriptionMap.put("heavy snow", "Starker Schneefall");
            descriptionMap.put("partly cloudy", "Teilweise wolkig");
            descriptionMap.put("thundershowers", "Gewitter");
            descriptionMap.put("snow showers", "Schneeschauer");
            descriptionMap.put("isolated thundershowers", "Isoliert Gewitter");
            //added manually
            descriptionMap.put("clear", "Klar");
            descriptionMap.put("mostly cloudy", "Größtenteils bewölkt");
        }
        return descriptionMap;
    }
}
