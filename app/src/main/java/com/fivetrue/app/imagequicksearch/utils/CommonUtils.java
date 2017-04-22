package com.fivetrue.app.imagequicksearch.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kwonojin on 2017. 3. 26..
 */

public class CommonUtils {

    private static final String TAG = "CommonUtils";


    public static Typeface getFont(Context context, String font){
        return Typeface.createFromAsset(context.getAssets(),
                font);
    }

    public static String convertToCurrency(long value){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(value);
    }

    public static String getDate(Context context, String pattern, long millis){
        Locale locale = context.getResources().getConfiguration().locale;
        String formatted = DateFormat.getBestDateTimePattern(locale, pattern);
        SimpleDateFormat sdf = new SimpleDateFormat(formatted);
        return sdf.format(new Date(millis));
    }

    public static void goStore(Context context){
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    /**
     * Check network connectivity (wi-fi)
     * @param context
     * @return connected state
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected()) return true;
        if(LL.D) Log.d("NetworkManager.java", "isWifiConnected : false");
        return false;
    }

    /**
     * Check network connectivity (mobile)
     * @param context
     * @return connected state
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
            if(LL.D) Log.d("NetworkManager.java", "isMobileConnected : true");
            return true;
        }
        if(LL.D) Log.d("NetworkManager.java", "isMobileConnected : false");
        return false;
    }

    /**
     * Check network connectivity (mobile, wi-fi)
     * @return connected state
     */
    public static boolean isOnline(Context context) { // network 연결 상태 확인
        // 08 Nov 2016, mark.lee
        if (context == null)
            return false;

        boolean connected = false;

        if(isWifiConnected(context))
            return true;

        if(isMobileConnected(context))
            return true;

        return connected;
    }
}
