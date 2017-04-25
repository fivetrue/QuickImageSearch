package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.ImageListAdapter;
import com.fivetrue.app.imagequicksearch.ui.fragment.ImageDetailViewFragment;
import com.fivetrue.app.imagequicksearch.utils.DataManager;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class SearchActivity extends BaseImageListActivity<GoogleImage>{

    private static final String TAG = "SearchResultFragment";

    private static final String KEY_KEYWORD = "keyword";

    private boolean mCanLoad = true;
    private String mKeyword;
    private String mInputText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKeyword = getIntent().getStringExtra(KEY_KEYWORD);
        findKeyword();
    }

    private void findKeyword(){
        showProgress();
        DataManager.getInstance(this).findImage(getKeyword())
                .subscribe(googleImages -> setData(googleImages)
                        ,throwable -> findImageFailure(throwable));
    }

    private void findImageFailure(Throwable t){
        hideProgress();
        TrackingUtil.getInstance().report(t);
        Toast.makeText(this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "findImageFailure: ", t);

    }

    @Override
    protected BaseRecyclerAdapter<GoogleImage> makeAdapter(List<GoogleImage> data, BaseHeaderFooterAdapter.OnItemClickListener<GoogleImage> ll) {
        return new ImageListAdapter(data, ll);
    }

    @Override
    protected List<GoogleImage> getData() {
        if(getAdapter() != null){
            return getAdapter().getData();
        }
        return null;
    }


    public static Intent makeIntent(Context context, String q){
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(KEY_KEYWORD, q);
        return intent;
    }


    @Override
    public List<GoogleImage> getSelections() {
        return getAdapter().getSelections();
    }

    @Override
    public String getKeyword() {
        return mKeyword;
    }

    @Override
    protected boolean onItemLongClick(GoogleImage item) {
        addFragment(ImageDetailViewFragment.class
                , ImageDetailViewFragment.makeBundle(this, item), android.R.id.content, true);
        hideSoftKey();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getAdapter() != null && getAdapter().getSelections().size() > 0){
            getAdapter().clearSelection();
            getSelectionViewer().update();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void addData(List<GoogleImage> data) {
        super.addData(data);
        getSupportActionBar().setTitle(getKeyword() + "(" + getAdapter().getData().size() + ")");
    }

    private void loadExtraData(){
        if(mCanLoad && TextUtils.isEmpty(mInputText)){
            getAdapter().showFooterProgress(true);
            mCanLoad = false;
             DataManager.getInstance(this)
                    .findImages(getKeyword(), getAdapter().getData().size() / 100)
                    .subscribe(images -> {
                        getAdapter().showFooterProgress(false);
                        if(LL.D) Log.d(TAG, "load extra data = [" + images + "]");
                        mCanLoad = true;
                        if(images != null && images.size() > 0){
                            if(LL.D) Log.d(TAG, "load extra count = [" + images.size() + "]");
                            addData(images);
                        }else{
                            mCanLoad = false;
                        }
                    });
        }
    }

    @Override
    public void onSendFailed(GoogleImage failedImage) {
        super.onSendFailed(failedImage);
        if(getAdapter() != null && failedImage != null){
            if(LL.D) Log.d(TAG, "onSendFailed() called with: failedImage = [" + failedImage + "]");
            int index = getAdapter().getData().indexOf(failedImage);
            getAdapter().notifyItemRemoved(index);
            getAdapter().getData().remove(index);
            Toast.makeText(this, R.string.delete_failed_images_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onScrollToPreLoading() {
        super.onScrollToPreLoading();
        loadExtraData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        initSearchView((SearchView) menu.findItem(R.id.action_search).getActionView());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(android.R.string.dialog_alert_title)
                        .setMessage(R.string.delete_cached_images_message)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            ImageDB.getInstance().deleteCachedImages(getKeyword());
                            dialogInterface.dismiss();
                            finish();

                        }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean onQueryTextChange(String newText) {
        mInputText = newText;
        Observable.fromIterable(ImageDB.getInstance().findCachedImages(mInputText))
                .filter(image -> image.getKeyword().equalsIgnoreCase(getKeyword()))
                .map(image -> new GoogleImage(image))
                .toList().subscribe(images -> setData(images));
        return true;
    }

    @Override
    protected boolean onQueryTextSubmit(String query) {
        getAdapter().clear();
        mKeyword = query;
        findKeyword();
        return true;
    }
}
