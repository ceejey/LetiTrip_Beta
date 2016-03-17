package de.ehealth.project.letitrip_beta.handler.fitbit;

import android.os.AsyncTask;
import android.util.Log;

import org.scribe.model.Token;

/** This class is for the authentication over Oauth 1.0 over WEB with the help of Scribe library. First step is to
 * init the process and build the Service by the method initOauth(), which is used by the fragment FitBitInit. Very important after that is the
 * Webview Fragment, because after the requesttoken creation (firstoauthstep)  the must login in over the webview with his
 * Fitbit account and must admit the Letitrip App. After that the fitbit Server sends a redirect URL to the App with a verifier at the end. With this
 * Verifier and the RequestToken the goes to build an accessToken. This token shows the Fitbit Server that that is a legit Connection and now
 * User data can be downloaded.
 */
public class RequestTokenTask extends AsyncTask<Void, Void, Void> {
    Oauth oauth = Oauth.getmOauth();
    String oauthUrl = "";
    Token requestToken = null;

    protected Void doInBackground(Void... params) {
        try {
            requestToken = oauth.getmService().getRequestToken();
            oauthUrl = oauth.getmService().getAuthorizationUrl(requestToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        oauth.setmAuthUrl(oauthUrl);
        oauth.setmRequestToken(requestToken);
        return null;
    }

}
