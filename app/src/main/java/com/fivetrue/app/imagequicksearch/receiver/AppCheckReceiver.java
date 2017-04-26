package com.fivetrue.app.imagequicksearch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.preference.DefaultPreferenceUtil;

/**
 * Created by kwonojin on 2017. 4. 26..
 */

public class AppCheckReceiver extends BroadcastReceiver {

    private static final String TAG = "AppCheckReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(context != null && intent != null){
            String action = intent.getAction();
            if(action != null){
                if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
                    onReceiveBootCompleted(context, intent);
                }
            }
        }
    }

    private void onReceiveBootCompleted(Context context, Intent intent){
        if(LL.D)
            Log.d(TAG, "onReceiveBootCompleted() called with: context = [" + context + "], intent = [" + intent + "]");
        boolean useQuick = DefaultPreferenceUtil.getUseQuickSearch(context);
        if(useQuick){

        }
    }
}
