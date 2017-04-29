package com.fivetrue.app.imagequicksearch.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.app.AppDB;
import com.fivetrue.app.imagequicksearch.model.app.AppInfo;
import com.fivetrue.app.imagequicksearch.ui.adapter.AppListAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.utils.AppUtil;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;


/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class FavoriteAppListActivity extends BaseActivity {

    private static final String TAG = "FavoriteAppListActivity";

    private RecyclerView mRecyclerView;
    private AppListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_app_list);
        initView();
        loadData();
    }

    private void loadData(){
        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("image/*");
        AppUtil.getIntentApps(this, intent)
                .toList().subscribe(appList -> {
                setData(appList);
        }, throwable -> {
            Log.e(TAG, "loadData: ", throwable);
        });
    }

    private void setData(List<ResolveInfo> data){
        if(LL.D) Log.d(TAG, "setData() called with: data = [" + data + "]");
        if(mAdapter == null){
            mAdapter = new AppListAdapter(data, new BaseHeaderFooterAdapter.OnItemClickListener<ResolveInfo>() {
                @Override
                public void onItemClick(RecyclerView.ViewHolder holder, int pos, ResolveInfo item) {
                    AppInfo info = AppDB.getInstance().getAppInfo(item.activityInfo.packageName);
                    if(info == null){
                        PackageManager pm = getPackageManager();
                        AppInfo newAppInfo = new AppInfo();
                        newAppInfo.setFavorite(true);
                        newAppInfo.setTargetClass(item.activityInfo.name);
                        newAppInfo.setSharedCount(0);
                        newAppInfo.setAppName(item.loadLabel(pm).toString());
                        newAppInfo.setUpdateDate(System.currentTimeMillis());
                        newAppInfo.setPackageName(item.activityInfo.packageName);
                        newAppInfo.setSharedImageCount(0);
                        AppDB.getInstance().insertAppInfo(newAppInfo);
                        TrackingUtil.getInstance().setFavoriteApp(newAppInfo.getPackageName());
                        mAdapter.notifyItemChanged(pos);
                    }else{
                        AppDB.get().executeTransaction(realm -> {
                            info.setFavorite(!info.isFavorite());
                            if(info.isFavorite()){
                                TrackingUtil.getInstance().setFavoriteApp(info.getPackageName());
                            }
                            mAdapter.notifyItemChanged(pos);
                        });
                    }
                }

                @Override
                public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos, ResolveInfo item) {
                    return false;
                }
            });
            AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(mAdapter);
            mRecyclerView.setAdapter(adapter);
        }else{
            mAdapter.setData(data);
        }
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.favorite_share_app));

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_app_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(mAdapter.getItemViewType(position) == BaseHeaderFooterAdapter.HEADER ||
                        mAdapter.getItemViewType(position) == BaseHeaderFooterAdapter.FOOTER){
                    return 4;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator(){
            @Override
            public long getChangeDuration() {
                return 0;
            }
        });
    }
}
