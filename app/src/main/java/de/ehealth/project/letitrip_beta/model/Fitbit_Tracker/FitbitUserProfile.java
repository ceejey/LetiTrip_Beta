package de.ehealth.project.letitrip_beta.model.Fitbit_Tracker;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;

import de.ehealth.project.letitrip_beta.model.Fitbit_Tracker.FitBit_Oauth.Oauth;

/**
 * Created by Mirorn on 28.10.2015.
 */
public class FitbitUserProfile {

    private static FitbitUserProfile mActiveUser = new FitbitUserProfile();

    public static FitbitUserProfile getmActiveUser(){
        return mActiveUser;
    }

    private FitbitUserProfile() {
    }
    private String mEncodedId = "";
    private Token mAccessToken = null;
    private String mDisplayName = "";
    private String mFullname= "";
    private String mAvatar = "";
    private String mDateOfBirth = "";
    private String mGender = "";
    private String mHeight = "";
    private String mWeight = "";
    private String mMemberSince = "";
    private String mWeightUnit = "";
    private String mHeightUnit = "";
    private String mWaterUnit = "";


    public static void JsonToUserProfile(String jSon, Token accessToken) {
        // überprüft ob vorhanden wenn ja verwende diesen wenn nicht parse die Informationen
        //    für einen neuen User heraus
        if(!jSon.isEmpty()) {
            try {
                JSONObject obj = null;
                obj = new JSONObject(jSon).getJSONObject("user");
                String encodedId = obj.getString("encodedId");
                FitbitUserProfile profile = searchUser(encodedId);

                if (!profile.mEncodedId.equals(encodedId)) {
                    profile.mEncodedId = encodedId;
                    profile.mAccessToken = accessToken;
                    profile.mFullname = obj.getString("fullName");
                    profile.mDateOfBirth = obj.getString("dateOfBirth");
                    profile.mDisplayName = obj.getString("displayName");
                    profile.mAvatar = obj.getString("avatar");
                    profile.mHeight = obj.getString("height");
                    profile.mHeightUnit = obj.getString("heightUnit");
                    profile.mWeight = obj.getString("weight");
                    profile.mWeightUnit = obj.getString("weightUnit");
                    profile.mWaterUnit = obj.getString("waterUnit");
                    profile.mGender = obj.getString("gender");
                    profile.mMemberSince = obj.getString("memberSince");
                }
                    mActiveUser = profile;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public static FitbitUserProfile searchUser(String id){
        if(mActiveUser.getmEncodedId().equals(id)) {
            return mActiveUser;
        }
        return new FitbitUserProfile();
    }

    public static void saveUser(Context context){

        SharedPreferences pref = context.getSharedPreferences("userprofile", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("UserId",mActiveUser.getmEncodedId() );
        edit.putString("AccessToken", mActiveUser.getmAccessToken().getToken().toString());
        edit.putString("Secret", mActiveUser.getmAccessToken().getSecret().toString() );
        edit.putString("Fullname",mActiveUser.getmFullname() );
        edit.putString("DateOfBirth", mActiveUser.getmDateOfBirth() );
        edit.putString("DisplayName",mActiveUser.getmDisplayName() );
        edit.putString("Avatar", mActiveUser.getmAvatar() );
        edit.putString("Height",mActiveUser.getmHeight() );
        edit.putString("HeightUnit", mActiveUser.getmHeightUnit() );
        edit.putString("Weight",mActiveUser.getmWeight() );
        edit.putString("WeightUnit", mActiveUser.getmWeightUnit() );
        edit.putString("WaterUnit",mActiveUser.getmWaterUnit() );
        edit.putString("Gender", mActiveUser.getmGender() );
        edit.putString("MemberSince", mActiveUser.getmMemberSince() );
        edit.commit();
    }


    public static void loadUser(Context context){

        FitbitUserProfile profile = mActiveUser;
        SharedPreferences pref = context.getSharedPreferences("userprofile", context.MODE_PRIVATE);
        profile.mEncodedId = pref.getString("UserId","");
        profile.mAccessToken = new Token(pref.getString("AccessToken",""), pref.getString("Secret","" ));
        profile.mFullname = pref.getString("Fullname","");
        profile.mDateOfBirth = pref.getString("DateOfBirth","");
        profile.mDisplayName = pref.getString("DisplayName","");
        profile.mAvatar = pref.getString("Avatar","");
        profile.mHeight = pref.getString("Height","");
        profile.mHeightUnit = pref.getString("HeightUnit","");
        profile.mWeight = pref.getString("Weight","");
        profile.mWeightUnit = pref.getString("WeightUnit","");
        profile.mWaterUnit = pref.getString("WaterUnit","");
        profile.mGender = pref.getString("Gender","");
        profile.mMemberSince = pref.getString("MemberSince","");
        // initialisiere Oauth sodass neue Json vom server nach einem App neustart geladen werden können
        Oauth.getmOauth().setmAccessToken(profile.mAccessToken);

    }
/*
    public static void deleteConsumer(Context context){
        SharedPreferences pref = context.getSharedPreferences("user", context.MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.commit();

    }
*/
    public String getmWeightUnit() {
        return mWeightUnit;
    }

    public void setmWeightUnit(String mWeightUnit) {
        this.mWeightUnit = mWeightUnit;
    }

    public String getmHeightUnit() {
        return mHeightUnit;
    }

    public void setmHeightUnit(String mHeightUnit) {
        this.mHeightUnit = mHeightUnit;
    }

    public String getmWaterUnit() {
        return mWaterUnit;
    }

    public void setmWaterUnit(String mWaterUnit) {
        this.mWaterUnit = mWaterUnit;
    }

    public String getmFullname() {
        return mFullname;
    }

    public void setmFullname(String mFullname) {
        this.mFullname = mFullname;
    }
    public String getmEncodedId() {
        return mEncodedId;
    }

    public Token getmAccessToken() {
        return mAccessToken;
    }

    public void setmAccessToken(Token mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmAvatar() {
        return mAvatar;
    }

    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public String getmWeight() {
        return mWeight;
    }

    public void setmWeight(String mWeight) {
        this.mWeight = mWeight;
    }

    public String getmMemberSince() {
        return mMemberSince;
    }

    public void setmMemberSince(String mMemberSince) {
        this.mMemberSince = mMemberSince;
    }

    public String getmHeight() {
        return mHeight;
    }

    public void setmHeight(String mHeight) {
        this.mHeight = mHeight;
    }

    public String getmGender() {
        return mGender;
    }

    public void setmGender(String mGender) {
        this.mGender = mGender;
    }

    public String getmDateOfBirth() {
        return mDateOfBirth;
    }

    public void setmDateOfBirth(String mDateOfBirth) {
        this.mDateOfBirth = mDateOfBirth;
    }

    public void setmEncodedId(String mEncodedId) {
        this.mEncodedId = mEncodedId;
    }
}
