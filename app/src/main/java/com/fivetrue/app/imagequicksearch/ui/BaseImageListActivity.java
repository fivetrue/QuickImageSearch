package com.fivetrue.app.imagequicksearch.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public abstract class BaseImageListActivity <T> extends BaseActivity implements ImageSelectionViewer.ImageSelectionClient {

    private static final String TAG = "BaseImageListActivity";

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
        updateActionBarTitle();

        mScrollView = (NestedScrollView) findViewById(R.id.sv_base_image_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_base_image_list);

        mImageSelectionViewer = (ImageSelectionViewer) findViewById(R.id.layout_base_image_list_selection);
        mImageSelectionViewer.setSelectionClient(this);
        mRecyclerView.setLayoutManager(makeLayoutManager());
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

    protected void updateActionBarTitle(){
        getSupportActionBar().setTitle(getKeyword() + " ("+ getData().size() +")");
    }

    public void setData(List<T> data){
        if(data != null){
            if(mImageAdapter == null){
                mImageAdapter = makeAdapter(data, new BaseFooterAdapter.OnItemClickListener<T>() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, T item) {
                        mImageAdapter.toggle(holder.getLayoutPosition());
                        BaseImageListActivity.this.onItemClick(item);
                    }

                    @Override
                    public boolean onItemLongClick(RecyclerView.ViewHolder holder, T item) {
                        if(holder != null && item != null){
                            return BaseImageListActivity.this.onItemLongClick(item);
                        }
                        return false;
                    }
                });

                SlideInBottomAnimationAdapter adapter = new SlideInBottomAnimationAdapter(mImageAdapter);
                mRecyclerView.setAdapter(adapter);
            }else{
                mImageAdapter.setData(data);
            }
        }
    }

    protected void onItemClick(T item){
        update();
    }

    protected void update(){
        mImageSelectionViewer.update();

    }

    protected boolean onItemLongClick(T item){
        return false;
    }

    protected abstract BaseFooterAdapter<T> makeAdapter(List<T> data, BaseFooterAdapter.OnItemClickListener<T> ll);

    protected abstract List<T> getData();

    protected abstract LinearLayoutManager makeLayoutManager();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public BaseFooterAdapter<T> getAdapter(){
        return mImageAdapter;
    }

    @Override
    public void clearSelection() {
        if(mImageAdapter != null){
            mImageAdapter.clearSelection();
        }
    }

    @Override
    public void onSendFailed(GoogleImage failedImage) {

    }

    @Override
    public void onClickAction() {

    }

    public ImageSelectionViewer getSelectionViewer(){
        return mImageSelectionViewer;
    }
}
