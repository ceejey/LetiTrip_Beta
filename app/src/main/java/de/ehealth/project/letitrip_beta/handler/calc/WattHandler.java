package de.ehealth.project.letitrip_beta.handler.calc;

/**
 * Created by eHealth on 08.12.2015.
 */
public class WattHandler {

    /**
     * This method calculates the mechanic watts for cycling
     * @param weightPerson
     * @param weightBike
     * @param heightPerson
     * @param gravityAcceleration
     * @param speed
     * @param attitudeGain
     * @param distance
     * @param windSpeed
     * @param angleToWind
     * @param temperatureCelcius
     * @param airPressure
     * @param humidity
     * @param rollingResistCoeff
     * @param projectionBodyArea
     * @param dragCoeff
     * @return
     */
    public double calcWatts(Float weightPerson, Float weightBike, Float heightPerson, Float gravityAcceleration,
                            Float speed, Float attitudeGain, Float distance, Float windSpeed, Float angleToWind,
                            Float temperatureCelcius, Float airPressure, Float humidity, Float rollingResistCoeff,
                            Float projectionBodyArea, Float dragCoeff) {

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

        return pMech;
    }

    /**
     * This method calculates the metabolic watts for running.
     * @param weightPerson
     * @param heightPerson
     * @param gravityAcceleration
     * @param speed
     * @param attitudeGain
     * @param passedTime
     * @return
     */
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

    /**
     * This method calculates the burned calories for given watts.
     * @param pMech
     * @param passedTime
     * @return
     */
    //Wirkungsgrad beim Laufen / Fahrradfahren sind in etwa gleich 20 - 25 %
    public double calcKcal(double pMech, double passedTime){
        double kJMech = (pMech/1000) * passedTime;
        double kJMet = kJMech / 0.22F;
        double kcal = kJMet * 0.239006F;
        return kcal;
    }
}
