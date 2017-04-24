package com.fivetrue.app.imagequicksearch;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.fivetrue.app.imagequicksearch.database.RealmDB;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.net.NetworkServiceProvider;
import com.fivetrue.app.imagequicksearch.utils.AdUtil;
import com.fivetrue.app.imagequicksearch.utils.DataManager;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class ApplicationEx extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        NetworkServiceProvider.init(this);
        RealmDB.init(this);
        DataManager.getInstance(this);
        TrackingUtil.init(this);
    }
}
