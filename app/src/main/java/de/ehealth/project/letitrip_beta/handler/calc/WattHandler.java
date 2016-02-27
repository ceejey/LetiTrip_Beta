package de.ehealth.project.letitrip_beta.handler.calc;

import android.util.Log;

/**
 * Created by eHealth on 08.12.2015.
 */
public class WattHandler {

    //TODO: Cleanup and Parameter!
    public double calcWatts(Float weightPerson, Float weightBike, Float heightPerson, Float gravityAcceleration,
                            Float speed, Float attitudeGain, Float distance, Float windSpeed, Float angleToWind,
                            Float temperatureCelcius, Float airPressure, Float humidity, Float rollingResistCoeff,
                            Float projectionBodyArea, Float dragCoeff) {
        /*Float mFahrer = 75F;        //Gewicht Fahrer
        Float mFahrrad = 10F;       //Gewicht Fahrrad
        Float gr = 180F;            //Größe Fahrer
        Float g = 9.81F;            //Schwerebeschleunigung
        Float v = 4.166F;           //Geschwindigkeit Fahrrad m/s
        Float h = 250F;             //Höhengewinn nach Strecke in m
        Float d = 4900F;            //Stecke in m
        Float vl = 7.5F;            //Windgeschwindigkeit in m/s
        Float gamma = 90F;          //Winkel zwischen Fahrt- und Windrichtung
        Float tc = 14F;             //Temperatur in °C
        Float p = 101020F;          //Luftdruck
        Float phi = 0.83F;          //Luftfeuchtigkeit
        Float cr = 0.007F;          //Rollwiderstandskonstante
        Float af = 0.276F;          //Anteil Projektionsfläche Oberkörperfläche
        Float cd = 1.1F;            //Luftwiderstandskonstante*/

        BicycleWattCalculator wc = new BicycleWattCalculator();

        Double fg = wc.calculateWeightForce(weightPerson + weightBike, gravityAcceleration);

        Double Cr = wc.calculateRollingResistance(rollingResistCoeff, fg);
        Double Cg = wc.calculateUphillResistance(weightPerson + weightBike, gravityAcceleration, attitudeGain, distance);
        Double a = Cr + Cg;
        Double es = wc.calculateSaturationVapour(temperatureCelcius);
        Double rf = wc.calculateGasConstant(humidity, es, airPressure);
        Double rho = wc.calculateAirDensity(airPressure, rf, (temperatureCelcius + 273.15F));
        Double A = wc.calculateProjectionSurface(projectionBodyArea, heightPerson, weightPerson + weightBike);
        Double b = wc.calculateAirResistance(dragCoeff, A, rho);
        Double pMech = wc.calculateMechWatts(a, b, speed, windSpeed, angleToWind);
        /*Log.d("Watt", "Watt:\nFg: " + fg + "\nCr: " + Cr + "\nCg: " + Cg + "\na: " + a + "\nes: " + es + "\nrf: " + rf + "\nrho: " + rho + "\nA: " +
                A + "\nb: " + b + "\npMech: " + pMech);*/

        return pMech;
    }

    //Should never get negative!!!!!
    public double calcRunningWatts(Float weightPerson, Float heightPerson, Float gravityAcceleration,
                                   Float speed, Float attitudeGain, Float passedTime){

        RunWattCalculator wc = new RunWattCalculator();

        //h = 2F;
        //passedTime = 100F; //In 100sek zb 2 meter hochgestiegen.
        Float stepsPerSecond = speed;
        Float focusHub = wc.calculateFocusHub(heightPerson);
        Double weightWatts = wc.calculateWeightWatts(weightPerson, gravityAcceleration, stepsPerSecond, focusHub);
        Double uphillWatts = wc.calculateUphillWatts(weightPerson, gravityAcceleration, attitudeGain, passedTime);
        Double pMet = wc.calculateRunningWatts(weightWatts, uphillWatts);


        return pMet;
    }

    //Wirkungsgrad beim Laufen / Fahrradfahren sind in etwa gleich 20 - 25 %
    public double calcKcal(double pMech, double pastTime){
        double kJMech = (pMech/1000) * pastTime;
        double kJMet = kJMech / 0.22F;
        double kcal = kJMet * 0.239006F;
        Log.d("Test", "Nach Formel: " + kcal);
        return kcal;
    }
}
