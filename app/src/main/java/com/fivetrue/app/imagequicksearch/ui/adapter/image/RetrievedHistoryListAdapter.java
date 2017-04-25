package com.fivetrue.app.imagequicksearch.ui.adapter.image;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.FooterHolder;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.RetrievedHistoryHolder;

import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class RetrievedHistoryListAdapter extends BaseRecyclerAdapter<CachedGoogleImage> {

    private static final String TAG = "ImageListAdapter";


    public RetrievedHistoryListAdapter(List<CachedGoogleImage> data
            , OnItemClickListener<CachedGoogleImage> ll){
        super(data);
        setOnItemClickListener(ll);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(Context context, int viewType) {
        return RetrievedHistoryHolder.makeHolder(context);
    }

    @Override
    protected void onBindHolder(RecyclerView.ViewHolder holder, int position) {
        CachedGoogleImage item = getItem(position);
        RetrievedHistoryHolder imageItemHolder = (RetrievedHistoryHolder)holder;
        imageItemHolder.setImage(item);
        imageItemHolder.layout.setOnClickListener(view -> onClickItem(imageItemHolder, position, item));
        imageItemHolder.layout.setOnLongClickListener(view -> onLongClickItem(imageItemHolder, position, item));
        imageItemHolder.check.setVisibility(View.GONE);
        imageItemHolder.text.setVisibility(View.VISIBLE);
        imageItemHolder.subText.setVisibility(View.VISIBLE);

        if(isEditMode()){
            imageItemHolder.text.setVisibility(View.GONE);
            imageItemHolder.subText.setVisibility(View.GONE);

            if(isSelect(position)){
                imageItemHolder.layout.animate()
                        .scaleX(1)
                        .scaleY(1)
                        .setDuration(300L)
                        .start();
                imageItemHolder.check.setVisibility(View.VISIBLE);
            }else{
                imageItemHolder.layout.animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(300L)
                        .start();
                imageItemHolder.check.setVisibility(View.GONE);
            }
        }
    }
}
