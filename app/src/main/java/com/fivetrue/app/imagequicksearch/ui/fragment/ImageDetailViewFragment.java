package com.fivetrue.app.imagequicksearch.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.preference.DefaultPreferenceUtil;
import com.fivetrue.app.imagequicksearch.utils.SimpleViewUtils;

/**
 * Created by kwonojin on 2017. 4. 25..
 */

public class ImageDetailViewFragment extends BaseFragment {

    private static final String TAG = "ImageDetailViewFragment";

    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_SITE = "site";
    private static final String KEY_SITE_URL = "siteUrl";
    private static final String KEY_PAGE = "page";
    private static final String KEY_PAGE_URL = "pageUrl";

    private View mLayout;
    private ImageView mImage;
    private TextView mSite;
//    private TextView mSiteUrl;
    private TextView mPage;
    private TextView mPageUrl;

    private ImageView mLike;

    private ProgressBar mProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_detail_view, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayout = view.findViewById(R.id.layout_fragment_image_detail);
        mImage = (ImageView) view.findViewById(R.id.iv_fragment_image_detail);
        mSite = (TextView) view.findViewById(R.id.tv_fragment_image_detail_site);
//        mSiteUrl = (TextView) view.findViewById(R.id.tv_fragment_image_detail_site_url);
        mPage = (TextView) view.findViewById(R.id.tv_fragment_image_detail_page);
        mPageUrl = (TextView) view.findViewById(R.id.tv_fragment_image_detail_page_url);
        mLike = (ImageView) view.findViewById(R.id.iv_fragment_image_detail_like);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_fragment_image_detail);
        view.setOnClickListener(view1 -> getFragmentManager().popBackStackImmediate());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String imageUrl = getArguments().getString(KEY_IMAGE_URL);
        String filePath = getArguments().getString(KEY_FILE_PATH);
        Glide.with(getActivity())
                .load(!TextUtils.isEmpty(filePath) ? filePath : imageUrl)
                .asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mImage.setImageBitmap(resource);
                mLayout.setVisibility(View.VISIBLE);
                mLayout.animate().alphaBy(0).alpha(1).setDuration(500L).start();
                if(getActivity() != null){
                    if(DefaultPreferenceUtil.isFirstOpen(getActivity(), getString(R.string.like_images))){
                        SimpleViewUtils.showSpotlight(getActivity(), mLike, getString(R.string.like_images)
                                , getString(R.string.spotlight_like_image_message), s -> {
                                    if(getActivity() != null){
                                        DefaultPreferenceUtil.setFirstOpen(getActivity(), getString(R.string.like_images), false);
                                        SimpleViewUtils.showSpotlight(getActivity(), mPageUrl, getString(R.string.source)
                                                , getString(R.string.spotlight_source_message), s1 -> {
                                                    if(getActivity() != null){
                                                        DefaultPreferenceUtil.setFirstOpen(getActivity(), getString(R.string.source), false);
                                                    }
                                                });
                                    }
                                });
                    }else if(DefaultPreferenceUtil.isFirstOpen(getActivity(), getString(R.string.source))){
                        SimpleViewUtils.showSpotlight(getActivity(), mPageUrl, getString(R.string.source)
                                , getString(R.string.spotlight_source_message), s1 -> {
                                    if(getActivity() != null){
                                        DefaultPreferenceUtil.setFirstOpen(getActivity(), getString(R.string.source), false);
                                    }
                                });
                    }
                }

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                getFragmentManager().popBackStackImmediate();
            }
        });
        mSite.setText(getArguments().getString(KEY_SITE));
//        mSiteUrl.setText(getArguments().getString(KEY_SITE_URL));
        mPage.setText(getArguments().getString(KEY_PAGE));
        mPageUrl.setText(getArguments().getString(KEY_PAGE_URL));

//        mSiteUrl.setOnClickListener(view -> goUrlPage(mSiteUrl.getText().toString()));
        mPageUrl.setOnClickListener(view -> goUrlPage(mPageUrl.getText().toString()));

        updateLike();

        mLike.setOnClickListener(view -> {
            if(view.isSelected()){
                CachedGoogleImage image = ImageDB.getInstance().findCachedImage(imageUrl);
                ImageDB.get().executeTransaction(realm -> {
                    image.setLike(false);
                    updateLike();
                });
            }else{
                CachedGoogleImage image = ImageDB.getInstance().findCachedImage(imageUrl);
                ImageDB.get().executeTransaction(realm -> {
                    image.setLike(true);
                    updateLike();
                });
            }
        });
    }

    private void updateLike(){
        if(mLike != null){
            CachedGoogleImage image = ImageDB.getInstance().findCachedImage(getArguments().getString(KEY_IMAGE_URL));
            mLike.setSelected(image != null && image.isLike());
        }
    }

    private void goUrlPage(String url){
        if(!TextUtils.isEmpty(url) && getActivity() != null){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }

    @Override
    public String getTitle(Context context) {
        return TAG;
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    public static Bundle makeBundle(Context context, GoogleImage image){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_IMAGE_URL, image.getOriginalImageUrl());
        bundle.putString(KEY_SITE, image.getSiteTitle());
        bundle.putString(KEY_SITE_URL, image.getSiteUrl());
        bundle.putString(KEY_PAGE, image.getSubject());
        bundle.putString(KEY_PAGE_URL, image.getPageUrl());
        return bundle;
    }

    public static Bundle makeBundle(Context context, CachedGoogleImage image){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_IMAGE_URL, image.getImageUrl());
        bundle.putString(KEY_SITE, image.getSiteTitle());
        bundle.putString(KEY_SITE_URL, image.getSiteUrl());
        bundle.putString(KEY_PAGE, image.getPageTitle());
        bundle.putString(KEY_PAGE_URL, image.getPageUrl());
        return bundle;
    }

    public static Bundle makeBundle(Context context, SavedImage image){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_IMAGE_URL, image.getImageUrl());
        bundle.putString(KEY_FILE_PATH, image.getFilePath());
        bundle.putString(KEY_SITE, image.getSiteTitle());
        bundle.putString(KEY_SITE_URL, image.getSiteUrl());
        bundle.putString(KEY_PAGE, image.getPageTitle());
        bundle.putString(KEY_PAGE_URL, image.getPageUrl());
        return bundle;
    }

}
