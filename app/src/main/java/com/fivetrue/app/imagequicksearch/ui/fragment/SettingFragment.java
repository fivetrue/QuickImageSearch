package com.fivetrue.app.imagequicksearch.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fivetrue.app.imagequicksearch.BuildConfig;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.preference.DefaultPreferenceUtil;
import com.fivetrue.app.imagequicksearch.service.QuickSearchService;
import com.fivetrue.app.imagequicksearch.utils.SimpleViewUtils;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import java.io.File;

/**
 * Created by kwonojin on 2017. 4. 25..
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = "SettingFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        getPreferenceScreen().findPreference(getString(R.string.pref_version)).setTitle(BuildConfig.VERSION_NAME);
        getPreferenceScreen().findPreference(getString(R.string.pref_reset_data)).setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.reset_data_message)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        TrackingUtil.getInstance()
                                .resetData(ImageDB.getInstance().getCachedImages().size()
                                        + ImageDB.getInstance().getSavedImages().size());
                        ImageDB.get().executeTransaction(realm -> {
                            for(SavedImage image : ImageDB.getInstance().getSavedImages()){
                                new File(image.getFilePath()).delete();
                            }
                            ImageDB.get().deleteAll();
                            dialogInterface.dismiss();
                            QuickSearchService.startQuickSearchService(getActivity());
                        });
                    }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                dialogInterface.dismiss();
            }).show();
            return true;
        });

        getPreferenceScreen().findPreference(getString(R.string.pref_quick_share_app)).setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.reset_data_message)
                    .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        TrackingUtil.getInstance()
                                .resetData(ImageDB.getInstance().getCachedImages().size()
                                        + ImageDB.getInstance().getSavedImages().size());
                        ImageDB.get().executeTransaction(realm -> {
                            for(SavedImage image : ImageDB.getInstance().getSavedImages()){
                                new File(image.getFilePath()).delete();
                            }
                            ImageDB.get().deleteAll();
                            dialogInterface.dismiss();
                            QuickSearchService.startQuickSearchService(getActivity());
                        });
                    }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
                dialogInterface.dismiss();
            }).show();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(getActivity() != null){
            if (s.equals(getString(R.string.pref_quick_menu))) {
                if(DefaultPreferenceUtil.isUsingQuickSearch(getActivity())){
                    QuickSearchService.startQuickSearchService(getActivity());
                }else{
                    QuickSearchService.stopQuickSearchService(getActivity());
                }
                TrackingUtil.getInstance().setQuickSearch(DefaultPreferenceUtil.isUsingQuickSearch(getActivity()));
            }
        }
    }
}
