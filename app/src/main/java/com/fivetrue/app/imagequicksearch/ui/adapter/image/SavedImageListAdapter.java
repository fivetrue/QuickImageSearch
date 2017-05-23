package com.fivetrue.app.imagequicksearch.ui.adapter.image;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.FooterHolder;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.SavedImageItemHolder;

import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class SavedImageListAdapter extends BaseRecyclerAdapter<SavedImage> {

    private static final String TAG = "ImageListAdapter";

    private boolean mShowHeader = true;
    private boolean mShowFooter = true;

    public SavedImageListAdapter(List<SavedImage> data, OnItemClickListener<SavedImage> ll){
        super(data);
        setOnItemClickListener(ll);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(Context context, int viewType) {
        return SavedImageItemHolder.makeHolder(context);
    }

    @Override
    protected void onBindHolder(RecyclerView.ViewHolder holder, int position) {
        SavedImage item = getItem(position);
        SavedImageItemHolder imageItemHolder = (SavedImageItemHolder)holder;
        Glide.with(imageItemHolder.image.getContext())
                .load(item.getFilePath())
                .asBitmap()
                .into(imageItemHolder.image);
        imageItemHolder.gif.setVisibility(item.getMimeType() != null && item.getMimeType().equalsIgnoreCase("gif")
                ?View.VISIBLE : View.GONE);
        imageItemHolder.layout.setOnClickListener(view -> onClickItem(imageItemHolder, position, item));
        imageItemHolder.layout.setOnLongClickListener(view -> onLongClickItem(imageItemHolder, position, item));

        if(isSelect(position)){
            imageItemHolder.layout.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(300L)
                    .start();
            imageItemHolder.check.setVisibility(View.VISIBLE);
        }else{
            imageItemHolder.layout.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setDuration(300L)
                    .start();
            imageItemHolder.check.setVisibility(View.GONE);
        }
    }

    public void setShowHeader(boolean b) {
        this.mShowHeader = b;
    }

    public void setShowFooter(boolean b) {
        this.mShowFooter = b;
    }

    @Override
    protected boolean isShowingFooter() {
        return mShowFooter;
    }

    @Override
    protected boolean isShowingHeader() {
        return mShowHeader;
    }
}
