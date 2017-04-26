package com.fivetrue.app.imagequicksearch.ui.adapter;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;

import com.fivetrue.app.imagequicksearch.ui.adapter.holder.AppInfoHolder;

import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 27..
 */

public class AppListAdapter extends BaseHeaderFooterAdapter<ResolveInfo> {

    public AppListAdapter(List<ResolveInfo> data, OnItemClickListener<ResolveInfo> ll) {
        super(data);
        setOnItemClickListener(ll);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterHolder(Context context, int viewType) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderHolder(Context context, int viewType) {
        return null;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHolder(Context context, int viewType) {
        return AppInfoHolder.makeHolder(context);
    }

    @Override
    protected void onBindHolder(RecyclerView.ViewHolder holder, int position) {
        ((AppInfoHolder)holder).setAppInfo(getItem(position));
        ((AppInfoHolder)holder).layout.setOnClickListener(view -> onClickItem(holder, position, getItem(position)));
    }

    @Override
    protected boolean isShowingFooter() {
        return false;
    }

    @Override
    protected boolean isShowingHeader() {
        return false;
    }
}
