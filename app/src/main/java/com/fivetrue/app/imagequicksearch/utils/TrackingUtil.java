package com.fivetrue.app.imagequicksearch.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

/**
 * Created by kwonojin on 2017. 4. 12..
 */

public class TrackingUtil {

    private static final String TAG = "TrackingUtil";

    private Context mContext;

    private static TrackingUtil sInstance;

    public static void init(Context context){
        sInstance = new TrackingUtil(context.getApplicationContext());
    }

    public static TrackingUtil getInstance(){
        return sInstance;
    }

    private TrackingUtil(Context context){
        mContext = context;
        FirebaseApp.initializeApp(mContext);
    }

    public void log(String tag, String message, Throwable t){
        FirebaseCrash.log(tag + "::" + message + "::" + t.getLocalizedMessage());
    }

    public void report(Throwable t){
        FirebaseCrash.report(t);
    }

    /**
     * to Analytics
     * @param event
     * @param b
     */
    public void logEvent(String event, Bundle b){
        FirebaseAnalytics.getInstance(mContext).logEvent(event, b);
    }

    /**
     * to Anlytics
     * @param a
     * @param screenName
     * @param screenClsOverride
     */
    public void currentScreen(Activity a, String screenName, String screenClsOverride){
        FirebaseAnalytics.getInstance(mContext).setCurrentScreen(a, screenName, screenClsOverride);
    }

    public void findImage(String keyword){
        Bundle b = new Bundle();
        b.putString("Keyword", keyword);
        FirebaseAnalytics.getInstance(mContext).logEvent("FindImage", b);
    }
}
