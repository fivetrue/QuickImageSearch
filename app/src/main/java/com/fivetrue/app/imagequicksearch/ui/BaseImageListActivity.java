package com.fivetrue.app.imagequicksearch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public abstract class BaseImageListActivity <T> extends BaseActivity implements ImageSelectionViewer.ImageSelectionClient {

    private static final String TAG = "BaseImageListActivity";

    private static final int PRE_LOADING_COUNT = 30;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgress;

    private BaseRecyclerAdapter<T> mImageAdapter;
    private ImageSelectionViewer mImageSelectionViewer;

    private LinearLayoutManager mLayoutManager;

    private SearchView mSearchView;
    private SearchManager mSearchManager;

    private InputMethodManager mImm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_image_list);
        initData();
        initView();
        setData(getData());
    }

    private void initData(){
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    }


    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_base_image_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_base_image_list);
        mProgress = (ProgressBar) findViewById(R.id.pb_base_image_list);

        mImageSelectionViewer = (ImageSelectionViewer) findViewById(R.id.layout_base_image_list_selection);
        mImageSelectionViewer.setSelectionClient(this);
        if(isGrid()){
            mLayoutManager = new GridLayoutManager(this, getSpanCount(), getOrientation(), false);
            ((GridLayoutManager)mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = mImageAdapter.getItemViewType(position);
                    if(viewType == mImageAdapter.HEADER || viewType == mImageAdapter.FOOTER){
                        return getSpanCount();
                    }else{
                        return 1;
                    }
                }
            });
        }else{
            mLayoutManager = new LinearLayoutManager(this, getOrientation(), false);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mImageAdapter != null){
                    if(RecyclerView.SCROLL_STATE_DRAGGING == newState
                            || RecyclerView.SCROLL_STATE_SETTLING == newState){
                        int lastVisiblePos = mLayoutManager.findLastVisibleItemPosition();
                        if(lastVisiblePos > mImageAdapter.getItemCount() - PRE_LOADING_COUNT){
                            onScrollToPreLoading();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerView.setItemAnimator(new DefaultItemAnimator(){
            @Override
            public long getChangeDuration() {
                return 0;
            }
        });
    }

    protected void updateActionBarTitle(){
        getSupportActionBar().setTitle(getKeyword() + " ("+ getData().size() +")");
    }

    public void setData(List<T> data){
        hideProgress();
        if(data != null){
            if(mImageAdapter == null){
                mImageAdapter = makeAdapter(data, new BaseHeaderFooterAdapter.OnItemClickListener<T>() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int pos,  T item) {
                        mImageAdapter.toggle(pos);
                        BaseImageListActivity.this.onItemClick(item);
                    }

                    @Override
                    public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos,  T item) {
                        if(holder != null && item != null){
                            return BaseImageListActivity.this.onItemLongClick(item);
                        }
                        return false;
                    }
                });
                mRecyclerView.setAdapter(mImageAdapter);
            }else{
                mImageAdapter.setData(data);
            }
            updateActionBarTitle();
        }
    }

    public void addData(List<T> data){
        if(data != null && mImageAdapter != null){
            mImageAdapter.getData().addAll(data);
            mImageAdapter.notifyDataSetChanged();
            updateActionBarTitle();
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

    protected abstract BaseRecyclerAdapter<T> makeAdapter(List<T> data, BaseHeaderFooterAdapter.OnItemClickListener<T> ll);

    protected abstract List<T> getData();

    protected boolean isGrid(){
        return true;
    }

    protected int getSpanCount(){
        return 3;
    }

    protected int getOrientation(){
        return LinearLayoutManager.VERTICAL;
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

    public BaseRecyclerAdapter<T> getAdapter(){
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

    protected void initSearchView(SearchView searchView) {
        mSearchView = searchView;
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(this.getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {
                return BaseImageListActivity.this.onQueryTextChange(newText);
            }

            public boolean onQueryTextSubmit(String query) {
                return BaseImageListActivity.this.onQueryTextSubmit(query);

            }
        };
        mSearchView.setOnQueryTextListener(queryTextListener);
    }

    protected void hideSoftKey(){
        if(mSearchView != null && mSearchView.getFocusedChild() != null
                && mSearchView.getFocusedChild().getWindowToken() != null){
            mImm.hideSoftInputFromWindow(mSearchView.getFocusedChild().getWindowToken(), 0);
        }
    }

    protected boolean onQueryTextChange(String newText){
        return false;
    }
    protected boolean onQueryTextSubmit(String query){
        hideSoftKey();
        return false;
    }

    protected void onScrollToPreLoading(){
        if(LL.D) Log.d(TAG, "onScrollToPreLoading() called");
    }

    protected void showProgress(){
        if(mProgress != null){
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgress(){
        if(mProgress != null){
            mProgress.setVisibility(View.GONE);
        }
    }
}
