package com.fivetrue.app.imagequicksearch.preference;

import android.content.Context;
import android.preference.PreferenceManager;

import com.fivetrue.app.imagequicksearch.R;

/**
 * Created by kwonojin on 2017. 3. 6..
 */

public class DefaultPreferenceUtil {

    private static final String TAG = "DefaultPreferenceUtil";

    private static final String KEY_NEW_PRODUCT_PERIOD = "new_product_period";
    private static final long DEFAULT_NEW_PRODUCT_PERIOD = 1000 * 60 * 60 * 24 * 3;

    public static void setNewProductPeriod(Context context, long period){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putLong(KEY_NEW_PRODUCT_PERIOD, period).commit();
    }

    public static long getNewProductPeriod(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_NEW_PRODUCT_PERIOD, DEFAULT_NEW_PRODUCT_PERIOD);
    }

//    public static void setUsingQuickSearch(Context context, boolean b){
//        PreferenceManager.getDefaultSharedPreferences(context)
//                .edit().putBoolean(context.getString(R.string.pref_quick_menu), b).commit();
//    }

    public static boolean isUsingQuickSearch(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.pref_quick_menu), true);
    }

    public static boolean isFirstOpen(Context context, String page){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(page, true);
    }

    public static void setFirstOpen(Context context, String page, boolean b){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(page, b).commit();
    }
}
