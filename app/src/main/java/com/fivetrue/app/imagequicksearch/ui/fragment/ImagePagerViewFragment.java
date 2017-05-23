package com.fivetrue.app.imagequicksearch.ui.fragment;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.fragment.ImagePagerFragmentAdapter;
import com.fivetrue.app.imagequicksearch.utils.DataManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 5. 22..
 */

public class ImagePagerViewFragment extends BaseFragment {

    private static final String TAG = "ImagePagerViewFragment";

    private static final String KEY_KEYWORD = "keyword";
    private static final String KEY_IMAGE_POS = "image_position";

    private ViewPager mViewPager;
    private ImagePagerFragmentAdapter mAdapter;

    private AdView mAdView;


    private List<GoogleImage> mData;
    private int mPos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String keyword = getArguments().getString(KEY_KEYWORD);
        mPos = getArguments().getInt(KEY_IMAGE_POS);
        mData = Observable.fromIterable(ImageDB.getInstance().getCachedImages(keyword))
                .map(image ->  new GoogleImage(image))
                .toList().blockingGet();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_pager_view, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdView = (AdView) view.findViewById(R.id.ad_fragment_image_pager);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_fragment_image_pager_view);
        mAdapter = new ImagePagerFragmentAdapter(mData, getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPos, false);
        initAd();
    }

    private void initAd(){
        DataManager.getInstance(getActivity()).getGeoLocation()
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

    @Override
    public String getTitle(Context context) {
        return TAG;
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    public static Bundle makeBundle(Context context, String keyword, int pos){
        Bundle b = new Bundle();
        b.putString(KEY_KEYWORD, keyword);
        b.putInt(KEY_IMAGE_POS, pos);
        return b;
    }
}
