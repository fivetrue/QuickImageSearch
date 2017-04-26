package com.fivetrue.app.imagequicksearch.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;

/**
 * Created by kwonojin on 2017. 4. 26..
 */

public class QuickSearchService extends Service {

    private static final String TAG = "QuickSearchService";

    private static final String ACTION_START_QUICK_SEARCH_SERVICE = "com.fivetrue.app.quicksearch.service.start";

    private static final int SERVICE_ID = 0x1209;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(LL.D)
            Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        if(intent != null){
            String action = intent.getAction();
            if(action != null){
                if(action.equals(ACTION_START_QUICK_SEARCH_SERVICE)){
                    onReceivedStart();
                }
            }
        }
        return START_STICKY;
    }

    private void onReceivedStart(){
        if(LL.D) Log.d(TAG, "onReceivedStart: ");
//        startForeground(SERVICE_ID, null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startQuickSearchService(Context context){
        Intent intent = new Intent(context, QuickSearchService.class);
        intent.setAction(ACTION_START_QUICK_SEARCH_SERVICE);
        context.startService(intent);
    }
}
