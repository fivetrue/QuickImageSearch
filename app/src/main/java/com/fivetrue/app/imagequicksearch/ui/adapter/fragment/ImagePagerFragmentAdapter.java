package com.fivetrue.app.imagequicksearch.ui.adapter.fragment;

import android.support.v4.app.FragmentManager;

import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFragmentPagerAdapter;
import com.fivetrue.app.imagequicksearch.ui.fragment.ImageDetailViewFragment;

import java.util.List;

/**
 * Created by kwonojin on 2017. 5. 23..
 */

public class ImagePagerFragmentAdapter extends BaseFragmentPagerAdapter {

    private List<GoogleImage> mData;

    public ImagePagerFragmentAdapter(List<GoogleImage> data, FragmentManager fm) {
        super(fm);
        mData = data;
    }

    @Override
    public Object getItem(int position) {
        ImageDetailViewFragment f = new ImageDetailViewFragment();
        f.setArguments(ImageDetailViewFragment.makeBundle(null, mData.get(position)));
        return f;
    }

    @Override
    public int getRealCount() {
        return mData.size();
    }

    @Override
    public int getVirtualPosition(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mData.size();
    }
}
