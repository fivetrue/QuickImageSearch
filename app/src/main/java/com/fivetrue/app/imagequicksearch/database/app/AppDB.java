package com.fivetrue.app.imagequicksearch.database.app;

import android.content.Context;

import com.fivetrue.app.imagequicksearch.database.RealmDB;
import com.fivetrue.app.imagequicksearch.model.app.AppInfo;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.Sort;

/**
 * Created by kwonojin on 2017. 4. 26..
 */

public class AppDB extends RealmDB implements RealmChangeListener {

    private static final String TAG = "AppDB";

    private Context mContext;
    private static AppDB sInstance;

    public static void init(Context context){
        sInstance = new AppDB(context.getApplicationContext());
    }

    public static AppDB getInstance(){
        return sInstance;
    }

    private AppDB(Context context){
        mContext = context;
    }


    public void insertAppInfo(AppInfo appInfo){
        get().executeTransaction(realm -> get().insertOrUpdate(appInfo));
    }

    public List<AppInfo> getAppInfoList(){
        return get().where(AppInfo.class).findAllSorted("updateDate", Sort.DESCENDING);
    }

    public AppInfo getAppInfo(String packageName){
        return get().where(AppInfo.class).equalTo("packageName",packageName).findFirst();
    }

    @Override
    public void onChange(Object element) {

    }
}
