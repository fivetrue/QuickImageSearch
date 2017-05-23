package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseHeaderFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseRecyclerAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.SavedImageListAdapter;
import com.fivetrue.app.imagequicksearch.ui.fragment.ImageDetailViewFragment;
import com.fivetrue.app.imagequicksearch.ui.fragment.ImagePagerViewFragment;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class SavedImageActivity extends BaseImageListActivity<SavedImage>{

    private static final String TAG = "SavedImageActivity";

    private String mKeyword;
    private boolean mOnlyGIf;

    @Override
    protected BaseRecyclerAdapter<SavedImage> makeAdapter(List<SavedImage> data, BaseHeaderFooterAdapter.OnItemClickListener<SavedImage> ll) {
        return new SavedImageListAdapter(data, ll);
    }

    @Override
    protected List<SavedImage> getData() {
        return Observable.fromIterable(ImageDB.getInstance().findSavedImages(mKeyword))
                .filter(savedImage -> {
                    if(mOnlyGIf){
                        return savedImage.getMimeType() != null
                                && savedImage.getMimeType().equalsIgnoreCase("gif");
                    }
                    return true;
                }).toList().blockingGet();
    }

    @Override
    protected int getSpanCount() {
        return 3;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home :
                onBackPressed();
                break;

            case R.id.action_delete:
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
                                                TrackingUtil.getInstance().deleteSavedImage(files.size());
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
        getMenuInflater().inflate(R.menu.menu_search, menu);
        initSearchView((SearchView) menu.findItem(R.id.action_search).getActionView());
        menu.findItem(R.id.action_gif).setOnMenuItemClickListener(menuItem -> {
            menuItem.setChecked(!menuItem.isChecked());
            mOnlyGIf = menuItem.isChecked();
            onQueryTextChange(mKeyword);
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public List<GoogleImage> getSelections() {
        return Observable.fromIterable(getAdapter().getSelections())
                .map(savedImage -> new GoogleImage(savedImage)).toList().blockingGet();
    }
    @Override
    protected boolean onItemLongClick(int pos, SavedImage item) {
        addFragment(ImagePagerViewFragment.class,
                ImagePagerViewFragment.makeBundle(this, item.getKeyword(), getData().indexOf(item), true), android.R.id.content, true);
        hideSoftKey();
        return true;
    }

    @Override
    public String getKeyword() {
        return getString(R.string.saved_images);
    }

    public static Intent makeIntent(Context context){
        Intent intent = new Intent(context, SavedImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected boolean onQueryTextChange(String newText) {
        clearSelection();
        mKeyword = newText;
        setData(getData());
        return true;
    }
}
