package com.fivetrue.app.imagequicksearch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.StoredImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.SavedImageListAdapter;
import com.fivetrue.app.imagequicksearch.ui.set.ImageLayoutSet;
import com.fivetrue.app.imagequicksearch.utils.DataManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private static final int STORED_IMAGE_ITEM_COUNT = 8;

    private NestedScrollView mScrollView;

    private ImageLayoutSet mSavedImageLayoutSet;
    private SavedImageListAdapter mSavedImageListAdapter;

    private ProgressBar mProgress;
    private SearchView mSearchView;

    private InputMethodManager mInputManager;
    private SearchManager mSearchManager;

    private Disposable mStoredImageDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mStoredImageDisposable != null && !mStoredImageDisposable.isDisposed()){
            mStoredImageDisposable.dispose();
        }
    }

    private void onLoadStoreImages(List<StoredImage> images){
        if(images != null){
            if(mSavedImageListAdapter == null){
                mSavedImageListAdapter = new SavedImageListAdapter(images, new BaseFooterAdapter.OnItemClickListener<StoredImage>() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, StoredImage item) {
                        Intent intent = new Intent(MainActivity.this, SavedImageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public boolean onItemLongClick(RecyclerView.ViewHolder holder, StoredImage item) {
                        return false;
                    }
                });
                mSavedImageLayoutSet.setAdapter(mSavedImageListAdapter);
                mSavedImageLayoutSet.animate().alphaBy(0).alpha(1).setDuration(500L).start();
                mSavedImageLayoutSet.setVisibility(View.VISIBLE);
            }else{
                mSavedImageListAdapter.setData(images);
            }
        }
    }

    private void initData(){
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mStoredImageDisposable = ImageDB.getInstance().getStoredImageObservable()
                .subscribe(images -> {
                    onLoadStoreImages(Observable.fromIterable(images)
                            .take(STORED_IMAGE_ITEM_COUNT).toList().blockingGet());
                });
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mScrollView = (NestedScrollView) findViewById(R.id.sv_main);
        mSavedImageLayoutSet = (ImageLayoutSet) findViewById(R.id.image_set_main_saved);
        mSavedImageLayoutSet.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        mSavedImageLayoutSet.setOnClickMoreListener(view -> {

        });
        mProgress = (ProgressBar) findViewById(R.id.pb_main);

        mScrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                mScrollView.smoothScrollTo(0, 0);
                mScrollView.removeOnLayoutChangeListener(this);
            }
        });

        ImageDB.getInstance().publishStoredImage();
    }

    private void findImageFailure(Throwable t){
        if(LL.D) Log.d(TAG, "findImageFailure() called with: t = [" + t + "]");
        Log.e(TAG, "findImageFailure: ", t);
        mProgress.setVisibility(View.GONE);

    }

    private void setData(String q, List<GoogleImage> images){
        mProgress.setVisibility(View.GONE);
        Intent intent = SearchResultActivity.makeIntent(this, q, new ArrayList<>(images));
        startActivity(intent);
//        Bundle b = SearchResultFragment.makeBundle(this, q, new ArrayList<>(images));
//        addFragment(SearchResultFragment.class, b, R.id.layout_main_anchor, true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {

                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                mInputManager.hideSoftInputFromWindow(mSearchView.getFocusedChild().getWindowToken(), 0);
                mProgress.setVisibility(View.VISIBLE);
                DataManager.getInstance(MainActivity.this).findImage(query)
                        .subscribe(googleImages -> setData(query, googleImages)
                                ,throwable -> findImageFailure(throwable));
                return true;
            }
        };
        mSearchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
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


}
