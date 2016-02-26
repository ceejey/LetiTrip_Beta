package de.ehealth.project.letitrip_beta.handler.calc;

/**
 * Created by Erich on 15.11.2015.
 */

class BicycleWattCalculator {

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
}
