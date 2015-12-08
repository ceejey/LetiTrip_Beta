package de.ehealth.project.letitrip_beta.view.fragment.settings.FitBit_Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.model.Fitbit_Tracker.FitBit_Oauth.Oauth;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 08.12.2015.
 */
public class WebviewOauth extends Fragment{

    private FragmentChanger mListener;
    WebView webView;
    Oauth mOauth = Oauth.getOauth();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.webview, container, false);
        mOauth.firstoauthstep();
        webView = (WebView)view.findViewById(R.id.webView);
        webView.setInitialScale(30);
        //webView.getSettings().setLoadWithOverviewMode(true);
        // webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(mOauth.getmRedirectUrl())) {
                    mOauth.getVerifierFromUrl(url);
                    mOauth.lastoauthstep();
                    if (mOauth.getmAccessToken() != null) {
                        Log.d("Fitbit", "Access Token !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        updateActivity(MainActivity.FragmentName.FIT_BIT_CHECKPROFILE);
                    }
                }
                return false;
            }
        });
        webView.loadUrl(mOauth.getmAuthUrl());
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentChanger) {
            mListener = (FragmentChanger) activity;
        } else {
            Log.d("Fitbit", "Wrong interface implemented");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }
}

