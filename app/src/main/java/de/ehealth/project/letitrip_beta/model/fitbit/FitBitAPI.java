package de.ehealth.project.letitrip_beta.model.fitbit;


import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * Created by Mirorn on 06.10.2015.
 * Hier stehen die URL´s für die Accesstokens der FitBit Api.
 * URL`s wurden von der Website vn FitBit entnommen
 */
public class FitBitAPI extends DefaultApi10a {

    private static final String AUTHORIZE_URL = "https://www.fitbit.com/oauth/authorize?oauth_token=";

    @Override
    public String getAccessTokenEndpoint()
    {
        return "https://api.fitbit.com/oauth/access_token";
    }

    @Override
    public String getRequestTokenEndpoint()
    {
        return "https://api.fitbit.com/oauth/request_token";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken)
    {
        return AUTHORIZE_URL +requestToken.getToken();
    }
}
