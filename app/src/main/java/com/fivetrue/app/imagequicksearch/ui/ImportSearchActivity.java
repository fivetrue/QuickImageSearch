package com.fivetrue.app.imagequicksearch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;
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
