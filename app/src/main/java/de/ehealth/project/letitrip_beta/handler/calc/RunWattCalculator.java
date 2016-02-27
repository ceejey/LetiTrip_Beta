package de.ehealth.project.letitrip_beta.handler.calc;

/**
 * Created by eHealth on 26.02.2016.
 */
public class RunWattCalculator {
    //height in cm
    public float calculateFocusHub(Float height){
        return 0.045f * (height/100f);
    }

    public double calculateWeightWatts(Float mass, Float gravityAcceleration, Float stepsPerSecond, Float focusHub){
        return mass * gravityAcceleration * stepsPerSecond * focusHub;
    }

    public double calculateRunningWatts(Double weightWatts, Double uphillWatts){
        return weightWatts + uphillWatts;
    }

    //pastTime in Seconds
    public double calculateUphillWatts(Float mass, Float heavyAcceleration, Float elevation, Float pastTime){
        return mass * heavyAcceleration * (elevation / pastTime);
    }
}
