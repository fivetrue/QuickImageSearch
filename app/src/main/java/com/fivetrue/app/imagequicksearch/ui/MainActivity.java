package com.fivetrue.app.imagequicksearch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.RetrievedImageListAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.SavedImageListAdapter;
import com.fivetrue.app.imagequicksearch.ui.fragment.ImageDetailViewFragment;
import com.fivetrue.app.imagequicksearch.ui.set.ImageLayoutSet;
import com.fivetrue.app.imagequicksearch.utils.CommonUtils;
import com.fivetrue.app.imagequicksearch.utils.DataManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements ImageSelectionViewer.ImageSelectionClient {

    private static final String TAG = "MainActivity";

    private static final int SAVED_IMAGE_ITEM_COUNT = 9;
    private static final int SAVED_IMAGE_ITEM_SPAN_COUNT = 3;

//    private static final int LIKE_IMAGE_ITEM_COUNT = 9;
    private static final int LIKE_IMAGE_ITEM_SPAN_COUNT = 3;

    private static final int RETREIVED_IMAGE_ITEM_COUNT = 10;
    private static final int RETREIVED_IMAGE_ITEM_SPAN_COUNT = 5;


    private NestedScrollView mScrollView;

    private ImageLayoutSet mRetrievedImageLayoutSet;
    private RetrievedImageListAdapter mRetrievedImageListAdapter;

    private ImageLayoutSet mSavedImageLayoutSet;
    private SavedImageListAdapter mSavedImageListAdapter;

    private ImageLayoutSet mLikeImageLayoutSet;
    private RetrievedImageListAdapter mLikeImageListAdapter;

    private AdView mAdView;
    private ImageSelectionViewer mImageSelectionViewer;

    private ViewGroup mLayoutAdAnchor;

    private ProgressBar mProgress;
    private SearchView mSearchView;

    private InputMethodManager mInputManager;
    private SearchManager mSearchManager;

    private Disposable mSavedImageDisposable;
    private Disposable mCachedImageDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSavedImageDisposable != null && !mSavedImageDisposable.isDisposed()){
            mSavedImageDisposable.dispose();
        }
        if(mCachedImageDisposable != null && !mCachedImageDisposable.isDisposed()){
            mCachedImageDisposable.dispose();
        }
    }

    private void onLoadStoreImages(List<SavedImage> images){
        if(images != null){
            if(mSavedImageListAdapter == null){
                mSavedImageListAdapter = new SavedImageListAdapter(images, new BaseHeaderFooterAdapter.OnItemClickListener<SavedImage>() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int pos,  SavedImage item) {
                        mSavedImageListAdapter.toggle(pos);
                        mImageSelectionViewer.update();
                    }

                    @Override
                    public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos, SavedImage item) {
                        addFragment(ImageDetailViewFragment.class
                                , ImageDetailViewFragment.makeBundle(MainActivity.this, item), android.R.id.content, true);
                        return true;
                    }
                });
                mSavedImageListAdapter.setShowHeader(false);
                mSavedImageListAdapter.setShowFooter(false);
            }else{
                mSavedImageListAdapter.setData(images);
            }

            if(images.size() > 0){
                mSavedImageLayoutSet.setAdapter(mSavedImageListAdapter);
                mSavedImageLayoutSet.animate().alphaBy(0).alpha(1).setDuration(500L).start();
                mSavedImageLayoutSet.setVisibility(View.VISIBLE);
            }else{
                mSavedImageLayoutSet.setVisibility(View.GONE);
            }
        }
    }

    private void onLoadRetrievedImages(List<CachedGoogleImage> images){
        if(images != null){
            if(mRetrievedImageListAdapter == null){
                mRetrievedImageListAdapter = new RetrievedImageListAdapter(images, new BaseHeaderFooterAdapter.OnItemClickListener<CachedGoogleImage>() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int pos, CachedGoogleImage item) {
                        startActivity(SearchActivity.makeIntent(MainActivity.this, item.getKeyword()));
                    }

                    @Override
                    public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos, CachedGoogleImage item) {
                        Toast.makeText(MainActivity.this
                                , CommonUtils.getDate(MainActivity.this, "yyyy-MM-dd hh:mm", item.getUpdateDate())
                                , Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }, true);
            }else{
                mRetrievedImageListAdapter.setData(images);
            }

            if(images.size() > 0){
                mRetrievedImageLayoutSet.setAdapter(mRetrievedImageListAdapter);
                mRetrievedImageLayoutSet.animate().alphaBy(0).alpha(1).setDuration(500L).start();
                mRetrievedImageLayoutSet.setVisibility(View.VISIBLE);
            }else{
                mRetrievedImageLayoutSet.setVisibility(View.GONE);
            }
        }
    }

    private void onLoadLikeImages(List<CachedGoogleImage> images){
        if(images != null){
            if(mLikeImageListAdapter == null){
                mLikeImageListAdapter = new RetrievedImageListAdapter(images, new BaseHeaderFooterAdapter.OnItemClickListener<CachedGoogleImage>() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int pos, CachedGoogleImage item) {
                        mLikeImageListAdapter.toggle(pos);
                        mImageSelectionViewer.update();
                    }

                    @Override
                    public boolean onItemLongClick(RecyclerView.ViewHolder holder, int pos, CachedGoogleImage item) {
                        addFragment(ImageDetailViewFragment.class
                                , ImageDetailViewFragment.makeBundle(MainActivity.this, item), android.R.id.content, true);
                        return true;
                    }
                }, false);
            }else{
                mLikeImageListAdapter.setData(images);
            }

            if(images.size() > 0){
                mLikeImageLayoutSet.setAdapter(mLikeImageListAdapter);
                mLikeImageLayoutSet.animate().alphaBy(0).alpha(1).setDuration(500L).start();
                mLikeImageLayoutSet.setVisibility(View.VISIBLE);
            }else{
                mLikeImageLayoutSet.setVisibility(View.GONE);
            }
        }
    }

    private void initData(){
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSavedImageDisposable = ImageDB.getInstance().getSavedImageObservable()
                .subscribe(images -> {
                    onLoadStoreImages(Observable.fromIterable(images)
                            .take(SAVED_IMAGE_ITEM_COUNT).toList().blockingGet());
                });

        mCachedImageDisposable = ImageDB.getInstance().getCachedImageObservable()
                .subscribe(savedImages -> {
                    onLoadRetrievedImages(Observable.fromIterable(savedImages)
                            .distinct(CachedGoogleImage::getKeyword)
                            .take(RETREIVED_IMAGE_ITEM_COUNT)
                            .toList().blockingGet());

                    onLoadLikeImages(Observable.fromIterable(savedImages)
                            .filter(image -> image.isLike())
//                            .take(LIKE_IMAGE_ITEM_COUNT)
                            .toList().blockingGet());
                });
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mScrollView = (NestedScrollView) findViewById(R.id.sv_main);

        mRetrievedImageLayoutSet = (ImageLayoutSet) findViewById(R.id.image_set_main_cached);
        mRetrievedImageLayoutSet.setLayoutManager(new GridLayoutManager(this, RETREIVED_IMAGE_ITEM_SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
        mRetrievedImageLayoutSet.setOnClickMoreListener(view -> startActivity(RetrievedHistoryActivity.makeIntent(this)));


        mLikeImageLayoutSet = (ImageLayoutSet) findViewById(R.id.image_set_main_like);
        mLikeImageLayoutSet.setLayoutManager(new GridLayoutManager(this, LIKE_IMAGE_ITEM_SPAN_COUNT, LinearLayoutManager.VERTICAL, false));


        mSavedImageLayoutSet = (ImageLayoutSet) findViewById(R.id.image_set_main_saved);
        mSavedImageLayoutSet.setLayoutManager(new GridLayoutManager(this, SAVED_IMAGE_ITEM_SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
        mSavedImageLayoutSet.setOnClickMoreListener(view -> startActivity(SavedImageActivity.makeIntent(this)));

        mLayoutAdAnchor = (ViewGroup) findViewById(R.id.layout_main_ad_anchor);

        mProgress = (ProgressBar) findViewById(R.id.pb_main);

        mAdView = (AdView) findViewById(R.id.ad_main);
        mImageSelectionViewer = (ImageSelectionViewer) findViewById(R.id.layout_main_image_selection);
        mImageSelectionViewer.setOnClickListener(null);
        mImageSelectionViewer.setSelectionClient(this);

        mScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                mScrollView.smoothScrollTo(0, 0);
                mScrollView.removeOnLayoutChangeListener(this);
            }
        });

        ImageDB.getInstance().publishSavedImage();
        ImageDB.getInstance().publishCachedImage();
    }

    private void initSearchView(SearchView searchView){

        mSearchView = searchView;
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {
                //TODO : total search.
                List<CachedGoogleImage> cachedGoogleImages = ImageDB.getInstance().findCachedImages(newText);
                onLoadRetrievedImages(Observable.fromIterable(cachedGoogleImages)
                        .distinct(CachedGoogleImage::getKeyword)
                        .toList().blockingGet());
                onLoadLikeImages(Observable
                        .fromIterable(cachedGoogleImages)
                        .filter(CachedGoogleImage::isLike)
                        .toList().blockingGet());
                onLoadStoreImages(ImageDB.getInstance().findSavedImages(newText));
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                //TODO : total search.
                mInputManager.hideSoftInputFromWindow(mSearchView.getFocusedChild().getWindowToken(), 0);
                startActivity(SearchActivity.makeIntent(MainActivity.this, query));
                return true;
            }
        };
        mSearchView.setOnQueryTextListener(queryTextListener);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        initSearchView((SearchView) menu.findItem(R.id.action_search).getActionView());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;

            case R.id.action_info :
                CommonUtils.goStore(this);
                break;

            case R.id.action_settings :
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public List<GoogleImage> getSelections() {
        Observable<GoogleImage> ob1 = Observable.fromIterable(mSavedImageListAdapter.getSelections())
                .map(savedImage -> new GoogleImage(savedImage));


        Observable<GoogleImage> ob2 = Observable.fromIterable(mLikeImageListAdapter.getSelections())
                .map(savedImage -> new GoogleImage(savedImage));

        return ob1.mergeWith(ob2).toList().blockingGet();
    }

    @Override
    public String getKeyword() {
        return null;
    }

    @Override
    public void clearSelection() {
        mSavedImageListAdapter.clearSelection();
        mLikeImageListAdapter.clearSelection();
    }

    @Override
    public void onSendFailed(GoogleImage failedImage) {

    }

    @Override
    public void onClickAction() {

    }

    @Override
    public void onBackPressed() {
        if(getSelections().size() > 0){
            mSavedImageListAdapter.clearSelection();
            mLikeImageListAdapter.clearSelection();
            mImageSelectionViewer.update();
            return;
        }
        super.onBackPressed();
    }
}
