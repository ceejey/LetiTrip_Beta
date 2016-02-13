package de.ehealth.project.letitrip_beta.handler.session;

/**
 * Created by eHealth on 31.12.2015.
 */
public class SessionHandler {
    //Kannst du vllt den ganzen Daten- und Methodenkram aus den Views in Handler- und Modelklassen packen?
    //Dann hast du wahrscheinlich keine Probleme mehr mit wechseln von Fragmenten und so, dadurch, dass sich alle
    //Views die gleichen Daten teilen können. Hab bei change fragment erstmal deine putint methode und so drin gelassen
    private static int mSelectedRunId = -1;

    public static int getSelectedRunId(){
        return mSelectedRunId;
    }
    public static void setSelectedRunId(int selectedRunId){
        mSelectedRunId = selectedRunId;
    }
}
