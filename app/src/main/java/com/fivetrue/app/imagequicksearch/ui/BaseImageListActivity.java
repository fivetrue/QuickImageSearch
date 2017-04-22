package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public abstract class BaseImageListActivity <T> extends BaseActivity implements ImageSelectionViewer.ImageSelectInfo{

    private static final String TAG = "SearchResultFragment";

    private static final String KEY_KEYWORD = "keyword";
    private static final String KEY_IMAGE_LIST = "image_list";

    private NestedScrollView mScrollView;
    private RecyclerView mRecyclerView;

    private BaseFooterAdapter<T> mImageAdapter;
    private ImageSelectionViewer mImageSelectionViewer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_image_list);
        initView();
        setData(getData());
    }


    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_base_image_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getKeyword() + " ("+ getData().size() +")");

        mScrollView = (NestedScrollView) findViewById(R.id.sv_base_image_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_base_image_list);

        mImageSelectionViewer = (ImageSelectionViewer) findViewById(R.id.layout_base_image_list_selection);
        mImageSelectionViewer.setImageSelectorInfo(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(mImageAdapter.getItemViewType(position) == BaseFooterAdapter.FOOTER){
                    return 3;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator(){
            @Override
            public long getChangeDuration() {
                return 0;
            }
        });

        mScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                mScrollView.smoothScrollTo(0, 0);
                mScrollView.removeOnLayoutChangeListener(this);
            }
        });
    }


    public void setData(List<T> data){
        if(data != null){
            if(mImageAdapter == null){
                mImageAdapter = makeAdapter(data, new BaseFooterAdapter.OnItemClickListener<T>() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, T item) {
                        mImageAdapter.toggle(holder.getLayoutPosition());
                        mImageSelectionViewer.update();
                    }

                    @Override
                    public boolean onItemLongClick(RecyclerView.ViewHolder holder, T item) {
                        if(holder != null && item != null){
                            return BaseImageListActivity.this.onItemLongClick(item);
                        }
                        return false;
                    }
                });

                AlphaInAnimationAdapter adapter = new AlphaInAnimationAdapter(mImageAdapter);
                mRecyclerView.setAdapter(adapter);
            }else{
                mImageAdapter.setData(data);
            }
        }
    }

    protected boolean onItemLongClick(T item){
        return false;
    }

    protected abstract BaseFooterAdapter<T> makeAdapter(List<T> data, BaseFooterAdapter.OnItemClickListener<T> ll);

    protected abstract List<T> getData();


    public static Intent makeIntent(Context context, String q, ArrayList<GoogleImage> images){
        Intent intent = new Intent(context, BaseImageListActivity.class);
        intent.putExtra(KEY_KEYWORD, q);
        intent.putParcelableArrayListExtra(KEY_IMAGE_LIST, images);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public BaseFooterAdapter getAdapter(){
        return mImageAdapter;
    }

    @Override
    public void clearSelection() {
        if(mImageAdapter != null){
            mImageAdapter.clearSelection();
        }
    }
}
