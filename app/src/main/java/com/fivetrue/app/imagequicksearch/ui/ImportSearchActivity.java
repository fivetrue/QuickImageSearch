package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.ImageListAdapter;
import com.fivetrue.app.imagequicksearch.ui.fragment.ImageDetailViewFragment;
import com.fivetrue.app.imagequicksearch.utils.DataManager;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class ImportSearchActivity extends SearchActivity{

    private static final String TAG = "ImportSearchActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(Intent intent){
        if(intent != null){
            String action = intent.getAction();
            if(action != null){
                if(action.equals(Intent.ACTION_SEND)){
                    onReceivedSendIntent(intent);
                }else{

                }
            }
        }
    }

    protected void onReceivedSendIntent(Intent intent){
        if(intent != null && intent.getAction() != null){
            if(intent.getAction().equals(Intent.ACTION_SEND)){
                Observable.range(0, intent.getClipData().getItemCount())
                        .map(integer-> intent.getClipData().getItemAt(integer))
                        .map(item -> item.getText().toString())
                        .toList().subscribe(strings -> {
                    setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    if(LL.D) Log.d(TAG, "onReceivedSendIntent: keywords " + strings);
                    String keyword = strings.get(0);
                    if(!TextUtils.isEmpty(keyword)){
                        setKeyword(keyword);
                    }
                },throwable -> {
                    Log.e(TAG, "onReceivedSendIntent: ", throwable);
                    TrackingUtil.getInstance().report(throwable);
                });

            }
        }
    }
}
