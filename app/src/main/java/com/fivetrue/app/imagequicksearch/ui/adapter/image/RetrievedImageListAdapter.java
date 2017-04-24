package com.fivetrue.app.imagequicksearch.ui.adapter.image;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.FooterHolder;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.RetrievedImageHolder;

import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class RetrievedImageListAdapter extends BaseFooterAdapter<CachedGoogleImage> {

    private static final String TAG = "ImageListAdapter";

    private boolean mShowText;

    public RetrievedImageListAdapter(List<CachedGoogleImage> data
            , OnItemClickListener<CachedGoogleImage> ll, boolean showText){
        super(data);
        setOnItemClickListener(ll);
        mShowText = showText;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterHolder(Context context, int viewType) {
        return FooterHolder.makeHolder(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(Context context, int viewType) {
        return RetrievedImageHolder.makeHolder(context);
    }

    @Override
    protected void onBindHolder(RecyclerView.ViewHolder holder, int position) {
        CachedGoogleImage item = getItem(position);
        RetrievedImageHolder imageItemHolder = (RetrievedImageHolder)holder;
        imageItemHolder.setImage(item);
        imageItemHolder.layout.setOnClickListener(view -> onClickItem(imageItemHolder, item));
        imageItemHolder.layout.setOnLongClickListener(view -> onLongClickItem(imageItemHolder, item));

        imageItemHolder.text.setVisibility(mShowText ? View.VISIBLE : View.GONE);
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

    @Override
    protected boolean showFooter() {
        return false;
    }
}
