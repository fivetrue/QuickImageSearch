package com.fivetrue.app.imagequicksearch.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.fragment.ImagePagerFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2017. 5. 22..
 */

public class ImagePagerViewFragment extends BaseFragment {

    private static final String TAG = "ImagePagerViewFragment";

    private static final String KEY_IMAGE_LIST = "image_list";
    private static final String KEY_IMAGE_POS = "image_position";

    private ViewPager mViewPager;
    private ImagePagerFragmentAdapter mAdapter;

    private List<GoogleImage> mData;
    private int mPos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = getArguments().getParcelableArrayList(KEY_IMAGE_LIST);
        mPos = getArguments().getInt(KEY_IMAGE_POS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_pager_view, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_fragment_image_pager_view);
        mAdapter = new ImagePagerFragmentAdapter(mData, getFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mPos, false);
    }

    @Override
    public String getTitle(Context context) {
        return TAG;
    }

    @Override
    public int getImageResource() {
        return 0;
    }

    public static Bundle makeBundle(Context context, ArrayList<GoogleImage> images, GoogleImage current){
        Bundle b = new Bundle();
        b.putParcelableArrayList(KEY_IMAGE_LIST, images);
        b.putInt(KEY_IMAGE_POS, images.indexOf(current));
        return b;
    }
}
