package de.ehealth.project.letitrip_beta.model.fitbit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eHealth on 15.03.2016.
 */
public class ActivityScoreSuggestion {
    Map<Float, String> suggestionMap = new HashMap<Float, String>();

    public ActivityScoreSuggestion(){
        suggestionMap.put(0.0F, "Fange jetzt an, indem du dein Fitbit Armband synchronisierst!");
        suggestionMap.put(0.1F, "Verbessere deinen ActivityScore indem du das Armband häufiger trägst!");
        suggestionMap.put(0.2F, "Dein Grundumsatz beeinflusst den ActivityScore auch. Verbessere ihn indem du das Armband trägst!");
        suggestionMap.put(0.3F, "Dein Grundumsatz beeinflusst den ActivityScore auch. Verbessere ihn indem du das Armband trägst!");
        suggestionMap.put(0.4F, "Es geht los! Laufe mehr um deinen Score zu verbessern.");
        suggestionMap.put(0.5F, "Da ist noch deutlich Luft nach oben");
        suggestionMap.put(0.6F, "Der Mensch verbrennt täglich durchschnittlich 2500 kcal. Du bist auf einem guten Weg!");
        suggestionMap.put(0.7F, "10000 Schritte ist das Optimum täglich. Weiter so!");
        suggestionMap.put(0.8F, "Der optimale ActivityScore liegt bei 1,0. Du bist fast soweit!");
        suggestionMap.put(0.9F, "Du hast es fast geschafft. Bei 1,0 bist du in deinem optimalen Fitnessbereich!");
        suggestionMap.put(1.0F, "Sehr gut. Du bist du in deinem optimalen Fitnessbereich!");
        suggestionMap.put(1.1F, "Du bist fitter als fit. Dein ActivityScore liegt über dem Optimum!");
    }

    public void getSuggestion(Float activityScore){

    }
}
