package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.RetrievedHistoryListAdapter;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class RetrievedHistoryActivity extends BaseImageListActivity<CachedGoogleImage>{

    private static final String TAG = "RetrievedImageActivity";

    private MenuItem mDeleteModeItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSelectionViewer().setSendAction(false);
    }

    @Override
    protected BaseFooterAdapter<CachedGoogleImage> makeAdapter(List<CachedGoogleImage> data, BaseFooterAdapter.OnItemClickListener<CachedGoogleImage> ll) {
        RetrievedHistoryListAdapter adapter = new RetrievedHistoryListAdapter(data, ll);
        adapter.setEditMode(false);
        return adapter;
    }

    @Override
    protected List<CachedGoogleImage> getData() {
        return Observable.fromIterable(ImageDB.getInstance().getCachedImages())
                .distinct(CachedGoogleImage::getKeyword)
                .toList().blockingGet();
    }

    @Override
    protected LinearLayoutManager makeLayoutManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(getAdapter().getItemViewType(position) == BaseFooterAdapter.FOOTER){
                    return 2;
                }
                return 1;
            }
        });
        return gridLayoutManager;
    }

    @Override
    protected void onItemClick(CachedGoogleImage item) {
        if(getAdapter().isEditMode()){
            super.onItemClick(item);
        }else{
            startActivity(RetrievedImageActivity.makeIntent(this, item));
        }
    }

    @Override
    protected boolean onItemLongClick(CachedGoogleImage item) {
        updateDeleteMode();
        getAdapter().toggle(getAdapter().getData().indexOf(item));
        getSelectionViewer().update();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;
            case R.id.action_delete_mode:
                updateDeleteMode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDeleteMode(){
        getAdapter().clearSelection();
        getAdapter().setEditMode(!getAdapter().isEditMode());
        mDeleteModeItem.setIcon(getAdapter().isEditMode()
                ? R.drawable.ic_cancel_accent_24dp : R.drawable.ic_delete_mode_accent_24dp);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mDeleteModeItem = menu.findItem(R.id.action_delete_mode);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_retrieved_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public List<GoogleImage> getSelections() {
        return Observable.fromIterable(getAdapter().getSelections())
                .map(savedImage -> new GoogleImage(savedImage)).toList().blockingGet();
    }

    @Override
    public String getKeyword() {
        return getString(R.string.retrieved_images);
    }

    public static final Intent makeIntent(Context context){
        Intent intent = new Intent(context, RetrievedHistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    @Override
    public void onClickAction() {
        super.onClickAction();
        new AlertDialog.Builder(this)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(R.string.delete_cached_images_message)
                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    List<CachedGoogleImage> selection = getAdapter().getSelections();
                    for(CachedGoogleImage image : selection){
                        getAdapter().notifyItemRemoved(getData().indexOf(image));
                        ImageDB.getInstance().deleteCachedImages(image);
                        getAdapter().getData().remove(image);
                    }
                    dialogInterface.dismiss();
                    finish();

                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {
        if(getAdapter() != null && getAdapter().isEditMode()){
            getAdapter().setEditMode(false);
            return;
        }
        super.onBackPressed();
    }
}
