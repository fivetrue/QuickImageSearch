package com.fivetrue.app.imagequicksearch.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.ViewGroup;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kwonojin on 2017. 4. 15..
 */

public class AdUtil extends AdListener {

    private static final String TAG = "AdUtil";

    private static final int REFRESH_COUNT = 2;

    private Context mContext;
    private AdRequest mAdRequest;
    private List<String> mKeywords;
    private String mUnitId;
    private AdView mAdView;
    private AdSize mAdSize;

    public AdUtil(Context context, List<String> keywords, String unitId){
        this(context, keywords, unitId, AdSize.LARGE_BANNER);
    }

    public AdUtil(Context context, List<String> keywords, String unitId, AdSize size){
        mContext = context;
        mUnitId = unitId;
        mAdSize = size;
        updateInfo(keywords);
    }

    public void updateInfo(List<String> keywords){
        if(keywords != null){
            mKeywords = keywords;
        }
        DataManager.getInstance(mContext).getGeoLocation()
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

        for(String keyword : mKeywords){
            request.addKeyword(keyword);
        }
        mAdView = makeAdView(mUnitId, mAdSize);
        mAdRequest = request.build();
        mAdView.loadAd(mAdRequest);
    }

    private AdView makeAdView(String unitId, AdSize adSize){
        AdView adView = new AdView(mContext);
        adView.setAdSize(adSize);
        adView.setAdUnitId(unitId);
        adView.setAdListener(this);
        return adView;
    }

    public void addAdView(ViewGroup parent, boolean refresh){
        if(mAdView != null && parent != null){
            Integer count = (Integer) mAdView.getTag();
            if((count != null && count > REFRESH_COUNT) || refresh){
                mAdView.loadAd(mAdRequest);
                mAdView.setTag(0);
            }
            if(count == null){
                count = 1;
            }
            count ++;
            mAdView.setTag(count);
            if(mAdView.getParent() == null){
                parent.addView(mAdView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }else{
                try{
                    ViewGroup viewGroup = (ViewGroup) mAdView.getParent();
                    viewGroup.removeView(mAdView);
                }catch (Exception e){
                    Log.e(TAG, "addAdView: ", e);
                    TrackingUtil.getInstance().report(e);
                }
            }
        }
    }

    public void detachAdView(){
        if(mAdView != null && mAdView.getParent() != null){
            ViewGroup parent = (ViewGroup) mAdView.getParent();
            parent.removeView(mAdView);
        }
    }

    @Override
    public void onAdClosed() {
        super.onAdClosed();
        Log.i(TAG, "onAdClosed: ");
    }

    @Override
    public void onAdFailedToLoad(int i) {
        super.onAdFailedToLoad(i);
        Log.i(TAG, "onAdFailedToLoad: " + i);
    }

    @Override
    public void onAdLeftApplication() {
        super.onAdLeftApplication();
        Log.i(TAG, "onAdLeftApplication: ");
    }

    @Override
    public void onAdOpened() {
        super.onAdOpened();
        Log.i(TAG, "onAdOpened: ");
    }

    @Override
    public void onAdLoaded() {
        super.onAdLoaded();
        Log.i(TAG, "onAdLoaded: ");
    }
}
