package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.RetrievedImageListAdapter;
import com.fivetrue.app.imagequicksearch.ui.fragment.ImageDetailViewFragment;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class RetrievedImageActivity extends BaseImageListActivity<CachedGoogleImage>{

    private static final String TAG = "RetrievedImageActivity";

    private static final String KEY_KEYWORD = "keyword";

    @Override
    protected BaseFooterAdapter<CachedGoogleImage> makeAdapter(List<CachedGoogleImage> data, BaseFooterAdapter.OnItemClickListener<CachedGoogleImage> ll) {
        return new RetrievedImageListAdapter(data, ll, false);
    }

    @Override
    protected List<CachedGoogleImage> getData() {
        return ImageDB.getInstance()
                .getCachedImages(getIntent().getStringExtra(KEY_KEYWORD));
    }

    @Override
    protected LinearLayoutManager makeLayoutManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(getAdapter().getItemViewType(position) == BaseFooterAdapter.FOOTER){
                    return 4;
                }
                return 1;
            }
        });
        return gridLayoutManager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;
            case R.id.action_delete_mode:
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retrieved_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public List<GoogleImage> getSelections() {
        return Observable.fromIterable(getAdapter().getSelections())
                .map(savedImage -> new GoogleImage(savedImage)).toList().blockingGet();
    }

    @Override
    public String getKeyword() {
        return getIntent().getStringExtra(KEY_KEYWORD);
    }

    public static final Intent makeIntent(Context context, CachedGoogleImage googleImage){
        Intent intent = new Intent(context, RetrievedImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY_KEYWORD, googleImage.getKeyword());
        return intent;
    }

    @Override
    protected boolean onItemLongClick(CachedGoogleImage item) {
        addFragment(ImageDetailViewFragment.class
                , ImageDetailViewFragment.makeBundle(this, item), android.R.id.content, true);
        return true;
    }

}
