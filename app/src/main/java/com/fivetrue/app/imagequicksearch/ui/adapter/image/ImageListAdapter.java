package com.fivetrue.app.imagequicksearch.ui.adapter.image;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.FooterHolder;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.ImageItemHolder;

import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class ImageListAdapter extends BaseRecyclerAdapter<GoogleImage> {

    private static final String TAG = "ImageListAdapter";


    public ImageListAdapter(List<GoogleImage> data, OnItemClickListener<GoogleImage> ll){
        super(data);
        setOnItemClickListener(ll);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(Context context, int viewType) {
        return ImageItemHolder.makeHolder(context);
    }

    @Override
    protected void onBindHolder(RecyclerView.ViewHolder holder, int position) {
        GoogleImage item = getItem(position);
        ImageItemHolder imageItemHolder = (ImageItemHolder)holder;
        imageItemHolder.setImage(item);
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
}
