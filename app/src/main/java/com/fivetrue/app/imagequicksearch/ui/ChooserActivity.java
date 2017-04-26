package com.fivetrue.app.imagequicksearch.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.app.AppInfo;
import com.fivetrue.app.imagequicksearch.provider.LocalFileProvider;
import com.fivetrue.app.imagequicksearch.ui.adapter.AppListAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.utils.AppUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 27..
 */

public class ChooserActivity extends Activity {

    private static final String TAG = "ChooserActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_URIS = "uris";

    private RecyclerView mAppList;
    private AppListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);
        initView();
        checkIntent();
    }

    private void initView(){
        mAppList = (RecyclerView) findViewById(R.id.rv_chooser);
        mAppList.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
    }

    private void checkIntent(){
        Intent intent = makeSendIntent(getIntent().getParcelableArrayListExtra(KEY_URIS));
        AppUtil.getIntentApps(this, intent)
                .toList()
                .subscribe(resolveInfos -> {
                    if(LL.D) Log.d(TAG, "chooser : resolveInfos = " + resolveInfos);
                    if(mAdapter == null){
                        mAdapter = new AppListAdapter(resolveInfos, new BaseHeaderFooterAdapter.OnItemClickListener<ResolveInfo>() {
                            @Override
                            public void onItemClick(RecyclerView.ViewHolder holder, int pos, ResolveInfo item) {

//                                AppInfo appInfo = new AppInfo();
//                                appInfo.setName(resolveInfo.activityInfo.name);
//                                appInfo.setPackageName(resolveInfo.activityInfo.packageName);
//                                appInfo.setTargetActivity(resolveInfo.activityInfo.targetActivity);
//                                appInfo.setIconResource(resolveInfo.activityInfo.getIconResource());
//                                item.setUpdateDate(System.currentTimeMillis());
//                                item.setSharedCount(item.getSharedCount() + 1);
//                                item.setSharedImageCount(item.getSharedImageCount() + getIntent().getParcelableArrayListExtra(KEY_URIS).size());
                                intent.setClassName(item.activityInfo.packageName, item.activityInfo.name);
                                startActivity(intent);
                            }

                            @Override
                            public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos, ResolveInfo item) {
                                return false;
                            }
                        });
                        mAppList.setAdapter(mAdapter);
                    }else{
                        mAdapter.setData(resolveInfos);
                    }
                }, throwable -> {
                    Log.e(TAG, "internalSend: getRunning task error", throwable);
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.send)));
                });
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

    public static void startActivity(Context context, List<File> files, String title){
        if(LL.D)
            Log.d(TAG, "startActivity() called with: context = [" + context + "], files = [" + files + "], title = [" + title + "]");
        Observable.fromIterable(files)
                .map(file -> LocalFileProvider.makeLocalFileUri(file))
                .toList().subscribe(uris -> {
            Intent intent = new Intent(context, ChooserActivity.class);
            intent.putExtra(KEY_TITLE , title);
            intent.putParcelableArrayListExtra(KEY_URIS, new ArrayList<>(uris));
            context.startActivity(intent);
        });

    }
}
