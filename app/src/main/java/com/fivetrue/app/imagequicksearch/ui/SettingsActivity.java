package com.fivetrue.app.imagequicksearch.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;


import com.fivetrue.app.imagequicksearch.BuildConfig;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.preference.DefaultPreferenceUtil;
import com.fivetrue.app.imagequicksearch.service.QuickSearchService;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;


/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    private NavigationView mNavigationView;

    private MenuItem mQuickSearchMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.settings) + " " + BuildConfig.VERSION_NAME);

        mNavigationView = (NavigationView) findViewById(R.id.navi_setting);
        View header = mNavigationView.getHeaderView(0);

        mQuickSearchMenuItem = mNavigationView.getMenu().findItem(R.id.menu_setting_quick_search);

        final String quickSearchTitle = mQuickSearchMenuItem.getTitle().toString();
        mQuickSearchMenuItem.setTitle(quickSearchTitle
                + " "
                + (DefaultPreferenceUtil.isUsingQuickSearch(this) ? "ON" : "OFF" ));
        mNavigationView.setNavigationItemSelectedListener(item -> {
            if(item != null){
                switch (item.getItemId()){
                    case R.id.menu_setting_reset_data :{
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setTitle(android.R.string.dialog_alert_title)
                                .setMessage(R.string.reset_data_message)
                                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                    TrackingUtil.getInstance()
                                            .resetData(ImageDB.getInstance().getCachedImages().size()
                                                    + ImageDB.getInstance().getSavedImages().size());
                                    ImageDB.get().executeTransaction(realm -> {
                                        ImageDB.get().deleteAll();
                                        dialogInterface.dismiss();
                                    });
                                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        }).show();
                    }
                       return true;
                    case R.id.menu_setting_quick_search :
                        boolean using = !DefaultPreferenceUtil.isUsingQuickSearch(this);
                        DefaultPreferenceUtil.setUseQuickSearch(SettingsActivity.this, using);
                        if(using){
                            QuickSearchService.startQuickSearchService(SettingsActivity.this);
                        }else{
                            QuickSearchService.stopQuickSearchService(SettingsActivity.this);
                        }
                        mQuickSearchMenuItem.setTitle(quickSearchTitle
                                + " "
                                + (using ? "ON" : "OFF" ));
                        return true;
                }
            }
            return false;
        });
    }



}
