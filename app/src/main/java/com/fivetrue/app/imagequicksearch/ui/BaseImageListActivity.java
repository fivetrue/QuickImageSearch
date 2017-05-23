package com.fivetrue.app.imagequicksearch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
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
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.app.imagequicksearch.utils.DataManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import io.reactivex.Observable;

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

    private AdView mAdView;

    private InputMethodManager mImm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_image_list);
        initData();
        initView();
        initAd();
        setData(getData());
    }

    private void initData(){
        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    }

    private void initAd(){
        DataManager.getInstance(this).getGeoLocation()
                .subscribe(geoLocation -> {
                    if(LL.D) Log.d(TAG, "loadAd: geoLocation : " + geoLocation);
                    if(geoLocation != null){
                        Location location = new Location(LocationManager.NETWORK_PROVIDER);
                        location.setAccuracy(geoLocation.getAccuracy());
                        location.setLatitude(geoLocation.getLocation().getLat());
                        location.setLongitude(geoLocation.getLocation().getLng());
                        setLocation(location);
                    }
                }, throwable -> {
                    Log.e(TAG, "fail getGeoLocation ", throwable);
                    setLocation(null);
                });
    }

    private void setLocation(Location location){
        final AdRequest.Builder request = new AdRequest.Builder();
        if(location != null){
            request.setLocation(location);
        }

        Observable.fromIterable(ImageDB.getInstance().getCachedImages())
                .distinct(CachedGoogleImage::getKeyword)
                .map(CachedGoogleImage::getKeyword)
                .toList().subscribe(strings -> {
            for(String keyword : strings){
                request.addKeyword(keyword);
            }
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Log.d(TAG, "onAdClosed() called");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.d(TAG, "onAdFailedToLoad() called with: i = [" + i + "]");
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                    Log.d(TAG, "onAdLeftApplication() called");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Log.d(TAG, "onAdOpened() called");
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }
            });
            mAdView.loadAd(request.build());
        });
    }

    private void initView(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_base_image_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_base_image_list);
        mProgress = (ProgressBar) findViewById(R.id.pb_base_image_list);
        mAdView = (AdView) findViewById(R.id.ad_base_image_list);

        mImageSelectionViewer = (ImageSelectionViewer) findViewById(R.id.layout_base_image_list_selection);
        mImageSelectionViewer.setOnClickListener(null);
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

    protected void setBackgroundColor(int color){
        findViewById(R.id.layout_base_image_list).setBackgroundColor(color);
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
                        BaseImageListActivity.this.onItemClick(holder, pos, item);
                    }

                    @Override
                    public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos,  T item) {
                        if(holder != null && item != null){
                            return BaseImageListActivity.this.onItemLongClick(pos, item);
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

    protected void onItemClick(RecyclerView.ViewHolder holder, int pos, T item){
        update();
    }

    protected void update(){
        mImageSelectionViewer.update();

    }

    protected boolean onItemLongClick(int pos, T item){
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

    protected View getCenterView(){
        return findViewById(R.id.view_base_image_list);
    }

    protected RecyclerView getRecyclerView(){
        return mRecyclerView;
    }
}
