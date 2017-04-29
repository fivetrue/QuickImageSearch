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

    public void deleteSavedImage(int count){
        Bundle b = new Bundle();
        b.putString("count", count +"");
        FirebaseAnalytics.getInstance(mContext).logEvent("DeleteSavedImage", b);
    }

    public void setQuickSearch(boolean b){
        Bundle bb = new Bundle();
        bb.putString("enable", b +"");
        FirebaseAnalytics.getInstance(mContext).logEvent("QuickSearch", bb);
    }

    public void setFavoriteApp(String packageName){
        Bundle b = new Bundle();
        b.putString("packageName", packageName);
        FirebaseAnalytics.getInstance(mContext).logEvent("FavoriteApp", b);
    }

    public void sendIntentFrom(String from, int count){
        Bundle b = new Bundle();
        b.putString("from", from);
        b.putString("count", count +"");
        FirebaseAnalytics.getInstance(mContext).logEvent("SendIntent", b);
    }

    public void resetData(int count){
        Bundle b = new Bundle();
        b.putString("ItemCount", count+"");
        FirebaseAnalytics.getInstance(mContext).logEvent("ResetData", b);
    }
}
