package com.fivetrue.app.imagequicksearch.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 26..
 */

public class QuickSearchService extends Service {

    private static final String TAG = "QuickSearchService";

    private static final String ACTION_START_QUICK_SEARCH_SERVICE = "com.fivetrue.app.quicksearch.service.start";
    private static final String ACTION_STOP_QUICK_SEARCH_SERVICE = "com.fivetrue.app.quicksearch.service.stop";

    private static final int SERVICE_ID = 0x1209;

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
                if(action.equals(ACTION_START_QUICK_SEARCH_SERVICE) && DefaultPreferenceUtil.isUsingQuickSearch(this)){
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
        setForeground();
//        setForeground(makeRemoteViews());
    }

    private void onReceivedStop(){
        stopForeground(true);
        stopSelf();
    }

    private void setForeground() {
        if(LL.D)
            if(LL.D) Log.d(TAG, "setForeground() called");
        Observable.fromIterable(ImageDB.getInstance().getSavedImages())
                .take(1)
                .subscribe(savedImage -> {
                    Bitmap bm = BitmapFactory.decodeFile(savedImage.getFilePath());
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                    builder.setSmallIcon(R.drawable.ic_search_accent_24dp);
                    builder.setContentTitle(getString(R.string.quick_search_menu));
                    builder.setContentText(getString(R.string.pull_down_menu));
                    builder.setColor(getResources().getColor(R.color.colorAccent));
                    builder.setPriority(Notification.PRIORITY_LOW);
                    builder.setOngoing(true);

                    NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                    bigPictureStyle.bigPicture(bm)
                            .setBigContentTitle(getString(R.string.last_sent_image))
                            .setSummaryText(getString(R.string.send_or_search));
                    builder.setStyle(bigPictureStyle);

                    Intent sendIntent = ChooserActivity.makeIntent(this, new File(savedImage.getFilePath()));
                    sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    PendingIntent sendPendingIntent = PendingIntent.getActivity(this, 0, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(R.drawable.ic_share_20dp, getString(R.string.send), sendPendingIntent);

                    Intent searchIntent = new Intent(this, ImportSearchActivity.class);
                    searchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    PendingIntent searchPendingIntent = PendingIntent.getActivity(this, 0, searchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.addAction(R.drawable.ic_search_accent_24dp, getString(R.string.search), searchPendingIntent);

                    Intent intent = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    startForeground(SERVICE_ID, builder.build());
                });
    }

    private RemoteViews makeRemoteViews(){
        List<SavedImage> images = getImages();
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_service_remote_view);
        for(int i = 0 ; i < images.size() ; i ++){
            SavedImage image = images.get(i);
            int imageViewId = REMOTE_IMAGE_VIEWS[i];
            if(image != null){
                File file = new File(image.getFilePath());
                remoteViews.setImageViewBitmap(imageViewId, BitmapFactory.decodeFile(image.getFilePath()));
                Intent intent = ChooserActivity.makeIntent(this, file);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(imageViewId, pendingIntent);
            }
        }

        Intent intent = new Intent(this, ImportSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_service_remote_search, pendingIntent);
        return remoteViews;
    }

    private List<SavedImage> getImages(){
//        if(DefaultPreferenceUtil.isSavedQuickPanel(this)){
            return Observable.fromIterable(ImageDB.getInstance().getSavedImages())
                    .take(IMAGE_COUNT)
                    .toList().blockingGet();

//        }else{
//            return Observable.fromIterable(ImageDB.getInstance().getCachedImages())
//                    .filter(CachedGoogleImage::isLike)
//                    .take(IMAGE_COUNT)
//                    .map(image -> new GoogleImage(image))
//                    .toList().blockingGet();
//        }
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
