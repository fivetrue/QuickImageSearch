package com.fivetrue.app.imagequicksearch.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.LikeImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.utils.ImageStoreUtil;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

/**
 * Created by kwonojin on 2017. 4. 25..
 */

public class ImageDetailViewFragment extends BaseFragment {

    private static final String TAG = "ImageDetailViewFragment";

    private static final String KEY_THUMBNAIL_URL = "thumbnail";
    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_FILE_PATH = "filePath";
    private static final String KEY_SITE = "site";
    private static final String KEY_SITE_URL = "siteUrl";
    private static final String KEY_PAGE = "page";
    private static final String KEY_PAGE_URL = "pageUrl";

    private ImageView mImage;
    private TextView mSite;
    private TextView mSiteUrl;
    private TextView mPage;
    private TextView mPageUrl;

    private ImageView mLike;

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
        mSiteUrl = (TextView) view.findViewById(R.id.tv_fragment_image_detail_site_url);
        mPage = (TextView) view.findViewById(R.id.tv_fragment_image_detail_page);
        mPageUrl = (TextView) view.findViewById(R.id.tv_fragment_image_detail_page_url);
        mLike = (ImageView) view.findViewById(R.id.iv_fragment_image_detail_like);
        String thumb = getArguments().getString(KEY_THUMBNAIL_URL);
        if(thumb != null){
            Glide.with(getActivity()).load(thumb).dontAnimate().into(mImage);
        }
        view.setOnClickListener(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String imageUrl = getArguments().getString(KEY_IMAGE_URL);
        String filePath = getArguments().getString(KEY_FILE_PATH);

        Glide.with(getActivity())
                .load(!TextUtils.isEmpty(filePath) ? filePath : imageUrl)
                .placeholder(mImage.getDrawable())
                .into(mImage);
        mSite.setText(getArguments().getString(KEY_SITE));
        mSiteUrl.setText(getArguments().getString(KEY_SITE_URL));
        mPage.setText(getArguments().getString(KEY_PAGE));
        mPageUrl.setText(getArguments().getString(KEY_PAGE_URL));

        mSiteUrl.setOnClickListener(view -> goUrlPage(mSiteUrl.getText().toString()));
        mPageUrl.setOnClickListener(view -> goUrlPage(mPageUrl.getText().toString()));

        updateLike();

        mLike.setOnClickListener(view -> {
            if(view.isSelected()){
                ImageDB.getInstance().deleteLikeImage(imageUrl);
                updateLike();
            }else{
                SavedImage savedImage = ImageDB.getInstance().findSavedImage(imageUrl);
                if(savedImage != null){
                    LikeImage likeImage = new LikeImage();
                    likeImage.setImageUrl(savedImage.getImageUrl());
                    likeImage.setUpdateDate(System.currentTimeMillis());
                    ImageDB.getInstance().insertLikeImage(likeImage);
                    updateLike();
                }else{
                    if(getActivity() != null){
                        CachedGoogleImage googleImage = ImageDB.getInstance().findCachedImage(imageUrl);
                        ImageStoreUtil.getInstance(getActivity())
                                .saveNetworkImage(new GoogleImage(googleImage), googleImage.getKeyword())
                                .subscribe(file -> {
                                    LikeImage likeImage = new LikeImage();
                                    likeImage.setImageUrl(imageUrl);
                                    likeImage.setUpdateDate(System.currentTimeMillis());
                                    ImageDB.getInstance().insertLikeImage(likeImage);
                                    updateLike();
                                }, throwable -> {
                                    TrackingUtil.getInstance().report(throwable);
                                    if(getActivity() != null){
                                        Toast.makeText(getActivity(), R.string.save_image_failure_message, Toast.LENGTH_SHORT).show();
                                    }
                                    updateLike();
                                });
                    }
                }
            }
        });
    }

    private void updateLike(){
        if(mLike != null){
            LikeImage image = ImageDB.getInstance().findLikeImage(getArguments().getString(KEY_IMAGE_URL));
            mLike.setSelected(image != null);
        }
    }

    private void goUrlPage(String url){
        if(!TextUtils.isEmpty(url) && getActivity() != null){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
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
        bundle.putString(KEY_THUMBNAIL_URL, image.getThumbnailUrl());
        bundle.putString(KEY_IMAGE_URL, image.getOriginalImageUrl());
        bundle.putString(KEY_SITE, image.getSiteTitle());
        bundle.putString(KEY_SITE_URL, image.getSiteUrl());
        bundle.putString(KEY_PAGE, image.getSubject());
        bundle.putString(KEY_PAGE_URL, image.getPageUrl());
        return bundle;
    }

    public static Bundle makeBundle(Context context, CachedGoogleImage image){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_THUMBNAIL_URL, image.getThumbnailUrl());
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
