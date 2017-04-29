package com.fivetrue.app.imagequicksearch.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import com.fivetrue.app.imagequicksearch.BuildConfig;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.preference.DefaultPreferenceUtil;
import com.fivetrue.app.imagequicksearch.service.QuickSearchService;
import com.fivetrue.app.imagequicksearch.utils.SimpleViewUtils;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import java.io.File;


/**
 * Created by kwonojin on 2017. 1. 23..
 */

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

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
        getSupportActionBar().setTitle(getString(R.string.settings));
    }
}
