package de.ehealth.project.letitrip_beta.handler.session;

public class SessionHandler {
    private static int mSelectedRunId = -1;

    private static int mRunType = 0; //1=bike; 0=walking

    public static int getRunType() {
        return mRunType;
    }

    public static void setRunType(int mRunType) {
        SessionHandler.mRunType = mRunType;
    }

    public static int getSelectedRunId(){
        return mSelectedRunId;
    }
    public static void setSelectedRunId(int selectedRunId){
        mSelectedRunId = selectedRunId;
    }
}
