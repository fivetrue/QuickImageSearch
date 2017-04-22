package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.StoredImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.ImageListAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.SavedImageListAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class SavedImageActivity extends BaseImageListActivity<StoredImage>{

    private static final String TAG = "SavedImageActivity";

    private static final String KEY_KEYWORD = "keyword";
    private static final String KEY_IMAGE_LIST = "image_list";


    @Override
    protected BaseFooterAdapter<StoredImage> makeAdapter(List<StoredImage> data, BaseFooterAdapter.OnItemClickListener<StoredImage> ll) {
        return new SavedImageListAdapter(data, ll);
    }

    @Override
    protected List<StoredImage> getData() {
        return ImageDB.getInstance().getStoredImages();
    }

    public static Intent makeIntent(Context context, String q, ArrayList<GoogleImage> images){
        Intent intent = new Intent(context, SavedImageActivity.class);
        intent.putExtra(KEY_KEYWORD, q);
        intent.putParcelableArrayListExtra(KEY_IMAGE_LIST, images);
        return intent;
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

    @Override
    public List<GoogleImage> getSelections() {
        return getAdapter().getSelections();
    }

    @Override
    public String getKeyword() {
        return getIntent().getStringExtra(KEY_KEYWORD);
    }

}
