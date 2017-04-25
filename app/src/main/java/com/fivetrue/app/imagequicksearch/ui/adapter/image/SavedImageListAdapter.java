package com.fivetrue.app.imagequicksearch.ui.adapter.image;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.FooterHolder;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.SavedImageItemHolder;

import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class SavedImageListAdapter extends BaseFooterAdapter<SavedImage> {

    private static final String TAG = "ImageListAdapter";


    public SavedImageListAdapter(List<SavedImage> data, OnItemClickListener<SavedImage> ll){
        super(data);
        setOnItemClickListener(ll);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterHolder(Context context, int viewType) {
        return FooterHolder.makeHolder(context);
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
                .placeholder(R.drawable.ic_default_thumbnail_50dp).into(imageItemHolder.image);
        imageItemHolder.layout.setOnClickListener(view -> onClickItem(imageItemHolder, item));
        imageItemHolder.layout.setOnLongClickListener(view -> onLongClickItem(imageItemHolder, item));

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
}
