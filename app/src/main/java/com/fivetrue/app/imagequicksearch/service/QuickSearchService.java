package com.fivetrue.app.imagequicksearch.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.preference.DefaultPreferenceUtil;
import com.fivetrue.app.imagequicksearch.ui.ChooserActivity;
import com.fivetrue.app.imagequicksearch.ui.ImportSearchActivity;
import com.fivetrue.app.imagequicksearch.ui.MainActivity;
import com.fivetrue.app.imagequicksearch.ui.SettingsActivity;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 26..
 */

public class QuickSearchService extends Service {

    private static final String TAG = "QuickSearchService";

    private static final String ACTION_START_QUICK_SEARCH_SERVICE = "com.fivetrue.app.quicksearch.service.start";
    private static final String ACTION_STOP_QUICK_SEARCH_SERVICE = "com.fivetrue.app.quicksearch.service.stop";

    private static final int SERVICE_ID = 0x1209;
    private static final int NOTIFICATION_ID = 0x0205;

    private static final int IMAGE_COUNT = 5;

    private static final int[] REMOTE_IMAGE_VIEWS = {
            R.id.iv_service_remote_image1,
            R.id.iv_service_remote_image2,
            R.id.iv_service_remote_image3,
            R.id.iv_service_remote_image4,
            R.id.iv_service_remote_image5,
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(LL.D)
            Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        if(intent != null){
            String action = intent.getAction();
            if(action != null){
                if(action.equals(ACTION_START_QUICK_SEARCH_SERVICE)){
                    onReceivedStart();
                }else if(action.equals(ACTION_STOP_QUICK_SEARCH_SERVICE)){
                    onReceivedStop();
                }
            }
        }
        return START_STICKY;
    }

    private void onReceivedStart(){
        if(LL.D) Log.d(TAG, "onReceivedStart: ");
        prepareService();
//        setForeground(makeRemoteViews());
    }

    private void onReceivedStop(){
        stopForeground(true);
        stopSelf();
    }

    private void prepareService() {
        List<SavedImage> list = ImageDB.getInstance().getSavedImages();
        if(list != null && !list.isEmpty()){
            Observable.fromIterable(list)
                    .take(1)
                    .timeout(5, TimeUnit.SECONDS)
                    .subscribe(savedImage -> {
                        setForeground(savedImage);
                    }, throwable -> {
                        Log.e(TAG, "setForeground: ", throwable);
                        setForeground(null);
                    });
        }else{
            setForeground(null);
        }
    }

    private void setForeground(SavedImage image){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_search_accent_24dp);
        builder.setContentTitle(getString(R.string.quick_search_menu));
        builder.setContentText(getString(R.string.pull_down_menu));
        builder.setColor(getResources().getColor(R.color.colorAccent));
        builder.setPriority(Notification.PRIORITY_LOW);

        if(image != null){
            File file = new File(image.getFilePath());
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(BitmapFactory.decodeFile(file.getAbsolutePath()))
                    .setBigContentTitle(getString(R.string.last_sent_image))
                    .setSummaryText(getString(R.string.send_or_search));
            builder.setStyle(bigPictureStyle);

            Intent sendIntent = ChooserActivity.makeIntent(this, file);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            PendingIntent sendPendingIntent = PendingIntent.getActivity(this, 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_share_accent_20dp, getString(R.string.send), sendPendingIntent);
        }

        Intent searchIntent = new Intent(this, ImportSearchActivity.class);
        searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent searchPendingIntent = PendingIntent.getActivity(this, 0, searchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_search_accent_24dp, getString(R.string.search), searchPendingIntent);

        Intent settingIntent = new Intent(this, SettingsActivity.class);
        searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent settingPendingIntent = PendingIntent.getActivity(this, 0, settingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_settings_accent_24dp, getString(R.string.settings), settingPendingIntent);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        if(DefaultPreferenceUtil.isUsingQuickSearch(this)){
            startForeground(SERVICE_ID, builder.build());
        }else{
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, builder.build());
        }

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
    public static void stopQuickSearchService(Context context){
        Intent intent = new Intent(context, QuickSearchService.class);
        intent.setAction(ACTION_STOP_QUICK_SEARCH_SERVICE);
        context.startService(intent);
    }

}
