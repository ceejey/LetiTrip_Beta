package de.ehealth.project.letitrip_beta.handler.calc;

/**
 * Created by eHealth on 08.12.2015.
 */
public class WattHandler {

    //TODO: Cleanup and Parameter!
    public double calcWatts() {
        Float mFahrer = 75F;        //Gewicht Fahrer
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
        Float cd = 1.1F;            //Luftwiderstandskonstante

        WattCalculator wc = new WattCalculator();

        Double fg = wc.calculateWeightForce(mFahrer + mFahrrad, g);
        Double Cr = wc.calculateRollingResistance(cr, fg);
        Double Cg = wc.calculateUphillResistance(mFahrer + mFahrrad, g, h, d);
        Double a = Cr + Cg;
        Double es = wc.calculateSaturationVapour(tc);
        Double rf = wc.calculateGasConstant(phi, es, p);
        Double rho = wc.calculateAirDensity(p, rf, (tc + 273.15F));
        Double A = wc.calculateProjectionSurface(af, gr, mFahrer + mFahrrad);
        Double b = wc.calculateAirResistance(cd, A, rho);
        Double pMech = wc.calculateMechWatts(a, b, v, vl, gamma);

        return pMech;
    }
}
