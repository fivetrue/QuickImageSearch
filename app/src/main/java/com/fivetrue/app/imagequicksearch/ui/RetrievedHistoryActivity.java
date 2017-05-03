package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.RetrievedHistoryListAdapter;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class RetrievedHistoryActivity extends BaseImageListActivity<CachedGoogleImage>{

    private static final String TAG = "RetrievedImageActivity";

    private MenuItem mDeleteModeItem;

    private Disposable mDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSelectionViewer().setSendAction(false);
        mDisposable = ImageDB.getInstance().getCachedImageObservable()
                .subscribe(cachedGoogleImages -> {
                    Observable.fromIterable(cachedGoogleImages)
                            .distinct(CachedGoogleImage::getKeyword)
                            .toList().subscribe(images -> setData(images));
                });

        ImageDB.getInstance().publishCachedImage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    @Override
    protected BaseRecyclerAdapter<CachedGoogleImage> makeAdapter(List<CachedGoogleImage> data, BaseHeaderFooterAdapter.OnItemClickListener<CachedGoogleImage> ll) {
        RetrievedHistoryListAdapter adapter = new RetrievedHistoryListAdapter(data, ll);
        adapter.setEditMode(false);
        return adapter;
    }

    @Override
    protected List<CachedGoogleImage> getData() {
        if(getAdapter() != null){
            return getAdapter().getData();
        }
        return null;
    }

    @Override
    protected int getSpanCount() {
        return 2;
    }

    @Override
    protected void onItemClick(RecyclerView.ViewHolder holder, int pos, CachedGoogleImage item) {
        if(getAdapter().isEditMode()){
            super.onItemClick(holder, pos, item);
        }else{
            startActivity(SearchActivity.makeIntent(this, item.getKeyword()));
        }
    }

    @Override
    protected boolean onItemLongClick(CachedGoogleImage item) {
        if(!getAdapter().isEditMode()){
            updateDeleteMode();
        }
//        getAdapter().toggle(getAdapter().getData().indexOf(item));
//        getSelectionViewer().update();
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
        getSelectionViewer().update();
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
                        getAdapter().getData().remove(image);
                        ImageDB.getInstance().deleteCachedImages(image);
                    }
                    dialogInterface.dismiss();

                    if(getAdapter().getData().size() == 0){
                        finish();
                        return;
                    }
                    onBackPressed();

                }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {
        if(getAdapter() != null && getAdapter().isEditMode()){
            getAdapter().clearSelection();
            getAdapter().setEditMode(false);
            getSelectionViewer().update();
            return;
        }
        super.onBackPressed();
    }

}
