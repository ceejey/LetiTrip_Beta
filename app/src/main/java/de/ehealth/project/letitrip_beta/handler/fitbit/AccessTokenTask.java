package de.ehealth.project.letitrip_beta.handler.fitbit;

import android.os.AsyncTask;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

public class AccessTokenTask extends AsyncTask<Void, Void, Void> {
    Oauth oauth = Oauth.getmOauth();
    Verifier verifier;
    Token accessToken = null;

    protected Void doInBackground(Void... params) {
        try {
            accessToken = oauth.getmService().getAccessToken(oauth.getmRequestToken(), verifier = new Verifier(oauth.getmVerifier()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        oauth.setmAccessToken(accessToken);
        return null;
    }

}
