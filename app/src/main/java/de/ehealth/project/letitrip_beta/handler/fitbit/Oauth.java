package de.ehealth.project.letitrip_beta.handler.fitbit;
import android.util.Log;
import android.webkit.URLUtil;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

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
                if (param.contains("verifier")) {
                    String name = param.split("=")[0];
                    String value = param.split("=")[1];
                    if (name.equals("oauth_verifier"))
                        Oauth.getOauth().setmVerifier(value);
                    return value;
                }
            }
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

