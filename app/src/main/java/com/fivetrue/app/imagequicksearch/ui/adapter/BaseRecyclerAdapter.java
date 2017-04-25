package com.fivetrue.app.imagequicksearch.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.app.imagequicksearch.ui.adapter.holder.FooterHolder;
import com.fivetrue.app.imagequicksearch.ui.adapter.holder.HeaderHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public abstract class BaseRecyclerAdapter<T> extends BaseHeaderFooterAdapter<T>{

    private static final String TAG = "BaseHeaderFooterAdapter";

    public BaseRecyclerAdapter(List<T> data){
        super(data);
    }

    private boolean mFooterProgress;

    @Override
    protected RecyclerView.ViewHolder onCreateFooterHolder(Context context, int viewType) {
        return FooterHolder.makeHolder(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderHolder(Context context, int viewType) {
        return HeaderHolder.makeHolder(context);
    }

    @Override
    protected void onBindFooterHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindFooterHolder(holder, position);
        FooterHolder footer = (FooterHolder) holder;
        footer.progressBar.setVisibility(mFooterProgress? View.VISIBLE : View.GONE);
    }

    public void showFooterProgress(boolean b){
        mFooterProgress = b;
        notifyItemChanged(getItemCount());
    }

    @Override
    protected boolean isShowingFooter() {
        return true;
    }

    @Override
    protected boolean isShowingHeader() {
        return true;
    }

}
