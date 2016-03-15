package de.ehealth.project.letitrip_beta.model.fitbit;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eHealth on 15.03.2016.
 */
public class ActivityScoreSuggestion {
    Map<Double, String> suggestionMap = new HashMap<Double, String>();

    public ActivityScoreSuggestion(){
        suggestionMap.put(0.0D, "Fange jetzt an, indem du dein Fitbit Armband synchronisierst!");
        suggestionMap.put(0.1D, "Verbessere deinen ActivityScore indem du das Armband häufiger trägst!");
        suggestionMap.put(0.2D, "Dein Grundumsatz beeinflusst den ActivityScore auch. Verbessere ihn indem du das Armband trägst!");
        suggestionMap.put(0.3D, "Dein Grundumsatz beeinflusst den ActivityScore auch. Verbessere ihn indem du das Armband trägst!");
        suggestionMap.put(0.4D, "Es geht los! Laufe mehr um deinen Score zu verbessern.");
        suggestionMap.put(0.5D, "Da ist noch deutlich Luft nach oben");
        suggestionMap.put(0.6D, "Der Mensch verbrennt täglich durchschnittlich 2500 kcal. Du bist auf einem guten Weg!");
        suggestionMap.put(0.7D, "10000 Schritte ist das Optimum täglich. Weiter so!");
        suggestionMap.put(0.8D, "Der optimale ActivityScore liegt bei 1,0. Du bist fast soweit!");
        suggestionMap.put(0.9D, "Du hast es fast geschafft. Bei 1,0 bist du in deinem optimalen Fitnessbereich!");
        suggestionMap.put(1.0D, "Sehr gut. Du bist du in deinem optimalen Fitnessbereich!");
        suggestionMap.put(1.1D, "Du bist fitter als fit. Dein ActivityScore liegt über dem Optimum!");
    }

    public String getSuggestion(Double activityScore){
        int scale = (int) Math.pow(10, 1);
        double actScore = (double) Math.round(activityScore * scale) / scale;

        if(actScore < 0)
            return suggestionMap.get(0.0D);
        if(actScore > 1.1F)
            return suggestionMap.get(1.1D);

        return suggestionMap.get(actScore);
    }
}
