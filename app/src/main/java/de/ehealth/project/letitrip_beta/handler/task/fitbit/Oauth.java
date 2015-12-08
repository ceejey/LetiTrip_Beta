package de.ehealth.project.letitrip_beta.handler.task.fitbit;

/**
 * Created by Mirorn on 06.10.2015.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.URLUtil;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/* Subclass AsyncTask which gets over the scribe service a request token.
 */
class RequestTokenTask extends AsyncTask<Void, Void, Void> {
    Oauth oauth = Oauth.getmOauth();
    String oauthUrl = "";
    Token requestToken = null;

    protected Void doInBackground(Void... params) {
        try {
            requestToken = oauth.getmService().getRequestToken();
            oauthUrl = oauth.getmService().getAuthorizationUrl(requestToken);
            Log.d("Fitbit", "Fertig");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Log.d("Fitbit", "Check connection");
        oauth.setmAuthUrl(oauthUrl);
        oauth.setmRequestToken(requestToken);
        return null;
    }

}

class AccessTokenTask extends AsyncTask<Void, Void, Void> {
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

public class Oauth {

    private String mConsumerKey = "";
    private String mConsumerSecret = "";
    private String mRedirectUrl = "";
    private OAuthService mService = null;
    private Token mRequestToken = null;
    private String mVerifier = null;
    private Token mAccessToken = null;
    private String mAuthUrl = "";
    private Class mApiClass = null;
    private static Oauth mOauth = new Oauth();

    private Oauth() {
    }

    public static Oauth getOauth() {
        return mOauth;
    }

    public void initOauth(String consumerKey, String consumerSecret, String redirectUrl, Class<? extends Api> apiClass) {

        mConsumerKey = consumerKey;
        mConsumerSecret = consumerSecret;
        mRedirectUrl = redirectUrl;
        mApiClass = apiClass;

        Log.d("Fitbit", "Consumer key: " + consumerKey);
        Log.d("Fitbit", "Consumer secret: " + consumerSecret);
        Log.d("Fitbit", "Redirect url: " + redirectUrl);
        Log.d("Fitbit", "Token: " + mRequestToken);

        mOauth.buildService(mOauth.getmApiClass());
    }

    /*
     * Erstellt ein Oauth Service Objekt für die Browser Variante der Autorisierung mit Redirect Url.
     * @param apiClass
     */
    public void buildService(Class<? extends Api> apiClass) {
        try {
            mService = new ServiceBuilder().provider(mApiClass)
                    .apiKey(mConsumerKey)
                    .apiSecret(mConsumerSecret)
                    .callback(mRedirectUrl)
                    .debug()
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void firstoauthstep() {
        try {
            new RequestTokenTask().execute().get();                                // asyncthread
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void lastoauthstep() {

        try {
            new AccessTokenTask().execute().get();                              // asyncthread
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getVerifierFromUrl(String url) {
        if (url != null && URLUtil.isValidUrl(url)) {
            String[] params = url.split("[&,?]");
            for (String param : params) {
                Log.d("Fitbit", param);
                if (param.contains("verifier")) {
                    String name = param.split("=")[0];
                    String value = param.split("=")[1];
                    if (name.equals("oauth_verifier"))
                        Oauth.getOauth().setmVerifier(value);
                    return value;
                }
            }
            Log.d("Fitbit", "No value for parameter");
        }

        return "";
    }

    public String getmConsumerKey() {
        return mConsumerKey;
    }

    public void setmConsumerKey(String mConsumerKey) {
        this.mConsumerKey = mConsumerKey;
    }

    public String getmConsumerSecret() {
        return mConsumerSecret;
    }

    public void setmConsumerSecret(String mConsumerSecret) {
        this.mConsumerSecret = mConsumerSecret;
    }

    public String getmRedirectUrl() {
        return mRedirectUrl;
    }

    public void setmRedirectUrl(String mRedirectUrl) {
        this.mRedirectUrl = mRedirectUrl;
    }

    public OAuthService getmService() {
        return mService;
    }

    public void setmService(OAuthService mService) {
        this.mService = mService;
    }

    public Token getmRequestToken() {
        return mRequestToken;
    }

    public void setmRequestToken(Token mRequestToken) {
        this.mRequestToken = mRequestToken;
    }

    public String getmVerifier() {
        return mVerifier;
    }

    public void setmVerifier(String mVerifier) {
        this.mVerifier = mVerifier;
    }

    public Token getmAccessToken() {
        return mAccessToken;
    }

    public void setmAccessToken(Token mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public String getmAuthUrl() {
        return mAuthUrl;
    }

    public void setmAuthUrl(String mAuthUrl) {
        this.mAuthUrl = mAuthUrl;
    }

    public static Oauth getmOauth() {
        return mOauth;
    }

    public Class getmApiClass() {
        return mApiClass;
    }

    public void setmApiClass(Class mApiClass) {
        this.mApiClass = mApiClass;
    }
}
