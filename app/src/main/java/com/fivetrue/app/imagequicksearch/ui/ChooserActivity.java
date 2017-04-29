package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.app.AppDB;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.app.AppInfo;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.preference.DefaultPreferenceUtil;
import com.fivetrue.app.imagequicksearch.provider.LocalFileProvider;
import com.fivetrue.app.imagequicksearch.ui.adapter.AppListAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.utils.AppUtil;
import com.fivetrue.app.imagequicksearch.utils.DataManager;
import com.fivetrue.app.imagequicksearch.utils.SimpleViewUtils;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by kwonojin on 2017. 4. 27..
 */

public class ChooserActivity extends BaseActivity {

    private static final String TAG = "ChooserActivity";

    private static final String KEY_URIS = "uris";

    private View mLayoutFavorite;

    private ImageView mFavoriteApp;
    private RecyclerView mFavoriteList;
    private AppListAdapter mFavoriteAdapter;

    private RecyclerView mAppList;
    private AppListAdapter mAdapter;

    private AdView mAdView;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        initData();
        initView();
        initAd();
        AppDB.getInstance().publishAppInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    private void initData(){
        mDisposable = AppDB.getInstance().getObservable()
                .subscribe(appInfos -> {
                    checkIntent(appInfos);
                });
    }

    private void initView(){
        mFavoriteApp = (ImageView) findViewById(R.id.iv_chooser_favorite_apps);
        mLayoutFavorite = findViewById(R.id.layout_chooser_favorite);
        mFavoriteList = (RecyclerView) findViewById(R.id.rv_chooser_favorite);
        mFavoriteList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mAppList = (RecyclerView) findViewById(R.id.rv_chooser);
        mAppList.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));

        mAdView = (AdView) findViewById(R.id.ad_chooser);

        mFavoriteApp.setOnClickListener(view -> startActivity(new Intent(this, FavoriteAppListActivity.class)));
    }

    private void initAd(){
        DataManager.getInstance(this).getGeoLocation()
                .subscribe(geoLocation -> {
                    if(LL.D) Log.d(TAG, "loadAd: geoLocation : " + geoLocation);
                    if(geoLocation != null){
                        Location location = new Location(LocationManager.NETWORK_PROVIDER);
                        location.setAccuracy(geoLocation.getAccuracy());
                        location.setLatitude(geoLocation.getLocation().getLat());
                        location.setLongitude(geoLocation.getLocation().getLng());
                        setLocation(location);
                    }
                }, throwable -> {
                    Log.e(TAG, "fail getGeoLocation ", throwable);
                    setLocation(null);
                });
    }

    private void setLocation(Location location){
        final AdRequest.Builder request = new AdRequest.Builder();
        if(location != null){
            request.setLocation(location);
        }

        Observable.fromIterable(ImageDB.getInstance().getCachedImages())
                .distinct(CachedGoogleImage::getKeyword)
                .map(CachedGoogleImage::getKeyword)
                .toList().subscribe(strings -> {
            for(String keyword : strings){
                request.addKeyword(keyword);
            }
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Log.d(TAG, "onAdClosed() called");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.d(TAG, "onAdFailedToLoad() called with: i = [" + i + "]");
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    Log.d(TAG, "onAdLeftApplication() called");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Log.d(TAG, "onAdOpened() called");
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }
            });
            mAdView.loadAd(request.build());
        });
    }

    private void checkIntent( List<AppInfo> apps){
        Intent intent = makeSendIntent(getIntent().getParcelableArrayListExtra(KEY_URIS));
        AppUtil.getIntentApps(this, intent)
                .toList().subscribe(appList -> {
            showSpotlight();
            Observable.fromIterable(appList)
                    .filter(resolveInfo -> {
                        for(AppInfo app : apps){
                            if(app.isFavorite() && app.getPackageName().equals(resolveInfo.activityInfo.packageName)){
                                return true;
                            }
                        }
                        return false;
                    }).toList().subscribe(resolveInfos -> setFavoriteData(intent, resolveInfos)
                    , throwable -> mLayoutFavorite.setVisibility(View.GONE));
            setData(intent, appList);
        }, throwable -> {
            sendFailure(intent, throwable);
        });
    }

    private void showSpotlight(){
        if(DefaultPreferenceUtil.isFirstOpen(this, getString(R.string.favorite_share_app))){
            SimpleViewUtils.showSpotlight(this, mFavoriteApp, getString(R.string.favorite_share_app)
                    , getString(R.string.favorite_share_app_message), s -> {
                        DefaultPreferenceUtil.setFirstOpen(this, getString(R.string.favorite_share_app), false);
                    });
        }
    }

    private void setFavoriteData(Intent intent, List<ResolveInfo> data){
        if(mFavoriteAdapter == null){
            mFavoriteAdapter = new AppListAdapter(data, new BaseHeaderFooterAdapter.OnItemClickListener<ResolveInfo>() {
                @Override
                public void onItemClick(RecyclerView.ViewHolder holder, int pos, ResolveInfo item) {
                    TrackingUtil.getInstance().sendIntentFrom("AppListFavorite"
                            , getIntent().getParcelableArrayListExtra(KEY_URIS).size());
                    sendIntent(intent, item);
                }

                @Override
                public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos, ResolveInfo item) {
                    return false;
                }
            });
            AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(mFavoriteAdapter);
            mFavoriteList.setAdapter(adapter);
        }else{
            mFavoriteAdapter.setData(data);
        }
        mLayoutFavorite.setVisibility(View.VISIBLE);
    }

    private void setData(Intent intent, List<ResolveInfo> data){
        if(LL.D) Log.d(TAG, "setData() called with: data = [" + data + "]");
        if(mAdapter == null){
            mAdapter = new AppListAdapter(data, new BaseHeaderFooterAdapter.OnItemClickListener<ResolveInfo>() {
                @Override
                public void onItemClick(RecyclerView.ViewHolder holder, int pos, ResolveInfo item) {
                    TrackingUtil.getInstance().sendIntentFrom("AppList"
                            , getIntent().getParcelableArrayListExtra(KEY_URIS).size());
                    sendIntent(intent, item);
                }

                @Override
                public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos, ResolveInfo item) {
                    return false;
                }
            });
            AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(mAdapter);
            mAppList.setAdapter(adapter);
        }else{
            mAdapter.setData(data);
        }
    }

    private Intent makeSendIntent(ArrayList<Uri> uris){
        Intent intent = null;
        if (uris.size() == 1) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0))
                    .setType("image/*");
        } else if (uris.size() > 1) {
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                    .setType("image/*");
        }
        return intent;
    }

    private void sendIntent(Intent intent, ResolveInfo resolveInfo){
        AppDB.get().executeTransaction(realm -> {
            AppInfo appInfo = AppDB.getInstance().getAppInfo(resolveInfo.activityInfo.packageName);
            if(appInfo == null){
                appInfo = new AppInfo();
            }
            CharSequence appName = resolveInfo.loadLabel(getPackageManager());
            appInfo.setAppName(appName != null ? appName.toString() : "");
            appInfo.setPackageName(resolveInfo.activityInfo.packageName);
            appInfo.setTargetClass(resolveInfo.activityInfo.name);
            appInfo.setUpdateDate(System.currentTimeMillis());
            appInfo.setSharedCount(appInfo.getSharedCount() + 1);
            appInfo.setSharedImageCount(appInfo.getSharedImageCount() + getIntent().getParcelableArrayListExtra(KEY_URIS).size());
            AppDB.get().insertOrUpdate(appInfo);
        });
        intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        startActivity(intent);
        finish();
    }

    private void sendFailure(Intent intent, Throwable throwable){
        TrackingUtil.getInstance().report(throwable);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.send)));
        finish();
    }


    public static Intent makeIntent(Context context, File file){
        ArrayList<File> arrayList = new ArrayList();
        arrayList.add(file);
        return makeIntent(context, arrayList);
    }

    public static Intent makeIntent(Context context, List<File> files){
        Intent intent = new Intent(context, ChooserActivity.class);
        List<Uri> uris = Observable.fromIterable(files)
                .map(file -> LocalFileProvider.makeLocalFileUri(file))
                .toList().blockingGet();
        intent.putParcelableArrayListExtra(KEY_URIS, new ArrayList<>(uris));
        return intent;
    }

    public static void startActivity(Context context, File file){
        ArrayList<File> arrayList = new ArrayList<>();
        arrayList.add(file);
        startActivity(context, arrayList);
    }

    public static void startActivity(Context context, List<File> files){
        context.startActivity(makeIntent(context, files));
    }
}
