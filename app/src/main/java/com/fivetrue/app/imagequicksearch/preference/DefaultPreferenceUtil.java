package com.fivetrue.app.imagequicksearch.preference;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by kwonojin on 2017. 3. 6..
 */

public class DefaultPreferenceUtil {

    private static final String TAG = "DefaultPreferenceUtil";

    private static final String KEY_NEW_PRODUCT_PERIOD = "new_product_period";
    private static final long DEFAULT_NEW_PRODUCT_PERIOD = 1000 * 60 * 60 * 24 * 3;

    private static final String KEY_USE_QUICK_SEARCH = "use_quick_search";

    public static void setNewProductPeriod(Context context, long period){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putLong(KEY_NEW_PRODUCT_PERIOD, period).commit();
    }

    public static long getNewProductPeriod(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(KEY_NEW_PRODUCT_PERIOD, DEFAULT_NEW_PRODUCT_PERIOD);
    }

    public static void setUseQuickSearch(Context context, boolean b){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putBoolean(KEY_USE_QUICK_SEARCH, b).commit();
    }

    public static boolean isUsingQuickSearch(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_USE_QUICK_SEARCH, true);

    }
}
