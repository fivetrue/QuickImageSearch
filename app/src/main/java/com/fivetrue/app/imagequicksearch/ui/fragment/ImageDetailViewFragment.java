package com.fivetrue.app.imagequicksearch.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.preference.DefaultPreferenceUtil;
import com.fivetrue.app.imagequicksearch.service.QuickSearchService;
import com.fivetrue.app.imagequicksearch.ui.ChooserActivity;
import com.fivetrue.app.imagequicksearch.utils.ImageStoreUtil;
import com.fivetrue.app.imagequicksearch.utils.SimpleViewUtils;

/**
 * Created by kwonojin on 2017. 4. 25..
 */

public class ImageDetailViewFragment extends BaseFragment {

    private static final String TAG = "ImageDetailViewFragment";

    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_THUMBNAIL_URL = "thumbnailUrl";
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_SITE = "site";
    private static final String KEY_SITE_URL = "siteUrl";
    private static final String KEY_PAGE = "page";
    private static final String KEY_PAGE_URL = "pageUrl";
    private static final String KEY_MIME_TYPE = "mimeType";

    private ImageView mImage;
    private TextView mSite;
//    private TextView mSiteUrl;
    private TextView mPage;
    private TextView mPageUrl;

    private ImageView mLike;
    private ImageView mShare;
    private ImageView mClose;

    private FloatingActionButton mLoadGif;
    private ProgressBar mProgressBar;

    private boolean mFailed;

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
        mImage = (ImageView) view.findViewById(R.id.iv_fragment_image_detail);
        mSite = (TextView) view.findViewById(R.id.tv_fragment_image_detail_site);
//        mSiteUrl = (TextView) view.findViewById(R.id.tv_fragment_image_detail_site_url);
        mPage = (TextView) view.findViewById(R.id.tv_fragment_image_detail_page);
        mPageUrl = (TextView) view.findViewById(R.id.tv_fragment_image_detail_page_url);
        mLike = (ImageView) view.findViewById(R.id.iv_fragment_image_detail_like);
        mShare = (ImageView) view.findViewById(R.id.iv_fragment_image_detail_share);
        mClose = (ImageView) view.findViewById(R.id.iv_fragment_image_detail_close);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_fragment_image_detail);
        mLoadGif = (FloatingActionButton) view.findViewById(R.id.fab_fragment_image_detail_gif_load);
        view.setOnClickListener(null);
        mClose.setOnClickListener(view1 -> {
            getFragmentManager().popBackStackImmediate();
        });

        mShare.setOnClickListener(view1 -> {
            if(getActivity() != null){
                String imageUrl = getArguments().getString(KEY_IMAGE_URL);
                CachedGoogleImage image = ImageDB.getInstance().findCachedImage(imageUrl);
                GoogleImage googleImage = new GoogleImage(image);
                ProgressDialog dialog = new ProgressDialog(getActivity());
                dialog.setTitle(R.string.send);
                dialog.setMessage(getActivity().getString(R.string.prepare_images_message));
                dialog.setCancelable(false);
                dialog.show();
                ImageStoreUtil.getInstance(getActivity())
                        .saveNetworkImage(googleImage, image.getKeyword())
                        .subscribe(file ->{
                            dialog.dismiss();
                            if(getActivity() != null){
                                QuickSearchService.startQuickSearchService(getActivity());
                                ChooserActivity.startActivity(getActivity(), file);
                            }
                        } ,throwable -> {
                            dialog.dismiss();
                            if(getActivity() != null){
                                Toast.makeText(getActivity(), R.string.send_image_failure_message, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "send failure: ", throwable);
                            }
                        });
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String imageUrl = getArguments().getString(KEY_IMAGE_URL);
        String filePath = getArguments().getString(KEY_FILE_PATH);
        String mimeType = getArguments().getString(KEY_MIME_TYPE);
        boolean isGif = mimeType != null && mimeType.equalsIgnoreCase("gif");
        Glide.with(getActivity())
                .load(!TextUtils.isEmpty(filePath) ? filePath : imageUrl)
                .asBitmap().into(new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                setImage(resource, null);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                Log.e(TAG, "onLoadFailed: ", e);
                onFailedLoadImage(this);
            }
        });

        if(isGif){
            mLoadGif.setVisibility(View.VISIBLE);
            mLoadGif.setOnClickListener(view1 -> {
                mProgressBar.setVisibility(View.VISIBLE);
                SimpleViewUtils.hideView(mLoadGif, View.GONE);
                Glide.with(getActivity()).load(!TextUtils.isEmpty(filePath) ? filePath : imageUrl)
                        .asGif().into(new SimpleTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                        setImage(null, resource);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        Log.e(TAG, "onLoadFailed: ", e);
                        SimpleViewUtils.showView(mLoadGif, View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        onFailedLoadImage(this);
                    }
                });
            });
        }
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

    private void setImage(Bitmap bm, GifDrawable gif){
        if(bm != null){
            Log.i(TAG, "setImage: is bitmap");
            mImage.setImageBitmap(bm);
        }else if(gif != null){
            Log.i(TAG, "setImage: is gif");
            mImage.setImageDrawable(gif);
            gif.start();
        }
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
    private void onFailedLoadImage(SimpleTarget simpleTarget){
        if(getActivity() != null){
            if(!mFailed){
                Log.e(TAG, "onLoadFailed: try again using thumbnail");
                Glide.with(getActivity()).load(getArguments().getString(KEY_THUMBNAIL_URL)).asBitmap().into(simpleTarget);
                mFailed = true;
                return;
            }
            getFragmentManager().popBackStackImmediate();
        }
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
        bundle.putString(KEY_THUMBNAIL_URL, image.getThumbnailUrl());
        bundle.putString(KEY_SITE, image.getSiteTitle());
        bundle.putString(KEY_SITE_URL, image.getSiteUrl());
        bundle.putString(KEY_PAGE, image.getSubject());
        bundle.putString(KEY_PAGE_URL, image.getPageUrl());
        bundle.putString(KEY_MIME_TYPE, image.getImageMimeType());
        return bundle;
    }

    public static Bundle makeBundle(Context context, CachedGoogleImage image){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_IMAGE_URL, image.getImageUrl());
        bundle.putString(KEY_THUMBNAIL_URL, image.getThumbnailUrl());
        bundle.putString(KEY_SITE, image.getSiteTitle());
        bundle.putString(KEY_SITE_URL, image.getSiteUrl());
        bundle.putString(KEY_PAGE, image.getPageTitle());
        bundle.putString(KEY_PAGE_URL, image.getPageUrl());
        bundle.putString(KEY_MIME_TYPE, image.getMimeType());
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
        bundle.putString(KEY_MIME_TYPE, image.getMimeType());
        return bundle;
    }

}
