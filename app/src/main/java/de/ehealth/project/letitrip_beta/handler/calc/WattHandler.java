package de.ehealth.project.letitrip_beta.handler.calc;

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
        /*float angleToWind = (float) Math.abs(windDirection-walkDirection);


        Log.w("session", "windAngle:" + windDirection + "--yourDirection:" + walkDirection+"angle:"+angleToWind);
        double watt = wattHandler.calcWatts( //TODO fehlende parameter
                75F,
                10F,
                180F,
                9.81F,
                (float) speedMperS,
                (float) GPSDatabaseHandler.getInstance().getData().getAltitudeDifference((((MainActivity)getActivity()).getGps().getActiveRecordingID()),-1),
                (float) dist,
                (float) (windSpeedKmH / 3.6),
                angleToWind,
                (float) temperature,
                (float) pressure,
                (float) (humidity)/100,
                0.007F,
                0.276F,
                1.1F

                    //TODO: Parameter names!
    public double calculateMechWatts(Double a, Double b, Float v, Float vl, Float gamma){
        return a * v + b * Math.pow(( v + vl * Math.cos( Math.toRadians(gamma) )),2) * v;
    }

    public double calculateRollingResistance(Float cr, Double fg){
        return cr * fg;
    }

    public double calculateWeightForce(Float m, Float g){
        return m * g;
    }

    public double calculateUphillResistance(Float m, Float g, Float h, Float d){
        return m * g * (h / d);
    }

    public double calculateAirResistance(Float cd, Double a, Double rho){
        return 0.5F * cd * a * rho;
    }

    public double calculateAirDensity(Float p, Double rf, Float tk){
        return p / (rf * tk);
    }

    public double calculateGasConstant(Float phi, Double es, Float p){
        return 287.058F / ( 1 - phi * es / (p * ( 1 - 287.058F / 461.523F)));
    }

    public double calculateSaturationVapour(Float tc){
        Float qc = 0F;
        Float rw = 0F;
        if(tc > 0){
            qc = 7.5F;
            rw = 237.3F;
        }
        else{
            qc = 7.6F;
            rw = 240.7F;
        }
        return 6.1078F * Math.pow(10, (qc * tc) / (rw + tc));
    }

    public double calculateProjectionSurface(Float af, Float gr, Float gw){
        return af * Math.sqrt(gr * gw / 3600);
    }
        );*/
        WattCalculator wc = new WattCalculator();

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
}
