package com.fivetrue.app.imagequicksearch.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;
import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 26..
 */

public class AppUtil {

    private static final String TAG = "AppUtil";

    public static Observable<AppInfo> getInstalledApps(Context context){
        PackageManager pm = context.getPackageManager();
        return Observable.fromIterable(pm.getInstalledPackages(PackageManager.GET_ACTIVITIES))
                .map(packageInfo -> {
                    if(LL.D) Log.d(TAG, "getRunningTaskList: name = " + packageInfo.packageName) ;
                    AppInfo appInfo = new AppInfo();
                    appInfo.packageInfo = packageInfo;
                    appInfo.appInfo = pm.getApplicationInfo(packageInfo.packageName,
                            PackageManager.GET_META_DATA);

                    appInfo.name = (String) pm.getApplicationLabel(pm
                            .getApplicationInfo(packageInfo.packageName,
                                    PackageManager.GET_META_DATA));
                    appInfo.icon = context.getPackageManager().getApplicationIcon(
                            packageInfo.packageName);
                    return appInfo;
                });
    }

    public static Observable<ResolveInfo> getIntentApps(Context context, Intent sendIntent){
        return Observable.fromIterable(context.getPackageManager().queryIntentActivities(sendIntent, 0));
    }

    public static final class AppInfo{
        public ApplicationInfo appInfo;
        public PackageInfo packageInfo;
        public String name;
        public Drawable icon;

        @Override
        public String toString() {
            return "AppInfo{" +
                    "appInfo=" + appInfo +
                    ", packageInfo=" + packageInfo +
                    ", name='" + name + '\'' +
                    ", icon=" + icon +
                    '}';
        }
    }
}
