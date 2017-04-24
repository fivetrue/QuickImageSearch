package com.fivetrue.app.imagequicksearch.ui;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.SavedImageListAdapter;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class SavedImageActivity extends BaseImageListActivity<SavedImage>{

    private static final String TAG = "SavedImageActivity";

    @Override
    protected BaseFooterAdapter<SavedImage> makeAdapter(List<SavedImage> data, BaseFooterAdapter.OnItemClickListener<SavedImage> ll) {
        return new SavedImageListAdapter(data, ll);
    }

    @Override
    protected List<SavedImage> getData() {
        return ImageDB.getInstance().getSavedImages();
    }

    @Override
    protected LinearLayoutManager makeLayoutManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        return gridLayoutManager;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;

            case R.id.action_delete :
                if(getAdapter().getSelections().size() > 0){
                    new AlertDialog.Builder(this)
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage(R.string.delete_saved_images_message)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                if (getAdapter() != null) {
                                    List<SavedImage> list = getAdapter().getSelections();
                                    Observable.fromIterable(list)
                                            .map(savedImage -> {
                                                getAdapter().notifyItemRemoved(getAdapter().getData().indexOf(savedImage));
                                                File path = new File(savedImage.getFilePath());
                                                return path;
                                            })
                                            .toList()
                                            .subscribe(files -> {
                                                ImageDB.getInstance().deleteSavedImages(list);
                                                Observable.fromIterable(files).subscribe(File::delete);
                                                getAdapter().clearSelection();
                                                dialogInterface.dismiss();
                                                update();
                                                setData(getData());
                                                if(getAdapter().getData().size() == 0){
                                                    finish();
                                                }else{
                                                    updateActionBarTitle();
                                                }
                                            });
                                }
                            }).setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public List<GoogleImage> getSelections() {
        return Observable.fromIterable(getAdapter().getSelections())
                .map(savedImage -> new GoogleImage(savedImage)).toList().blockingGet();
    }

    @Override
    protected void onItemClick(SavedImage item) {
        super.onItemClick(item);
    }

    @Override
    public String getKeyword() {
        return getString(R.string.saved_images);
    }

}
