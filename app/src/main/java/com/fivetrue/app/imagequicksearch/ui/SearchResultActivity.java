package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.ui.adapter.BaseFooterAdapter;
import com.fivetrue.app.imagequicksearch.ui.adapter.image.ImageListAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class SearchResultActivity extends BaseImageListActivity<GoogleImage>{

    private static final String TAG = "SearchResultFragment";

    private static final String KEY_KEYWORD = "keyword";
    private static final String KEY_IMAGE_LIST = "image_list";


    @Override
    protected BaseFooterAdapter<GoogleImage> makeAdapter(List<GoogleImage> data, BaseFooterAdapter.OnItemClickListener<GoogleImage> ll) {
        return new ImageListAdapter(data, ll);
    }

    @Override
    protected List<GoogleImage> getData() {
        return getIntent().getParcelableArrayListExtra(KEY_IMAGE_LIST);
    }

    @Override
    protected LinearLayoutManager makeLayoutManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        return gridLayoutManager;
    }

    public static Intent makeIntent(Context context, String q, ArrayList<GoogleImage> images){
        Intent intent = new Intent(context, SearchResultActivity.class);
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

    @Override
    protected boolean onItemLongClick(GoogleImage item) {
        Toast.makeText(this, item.getSiteTitle(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getAdapter() != null && getAdapter().getSelections().size() > 0){
            getAdapter().clearSelection();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSendFailed(GoogleImage failedImage) {
        super.onSendFailed(failedImage);
        if(getAdapter() != null && failedImage != null){
            int index = getAdapter().getData().indexOf(failedImage);
            getAdapter().notifyItemRemoved(index);
            getAdapter().getData().remove(index);
            Toast.makeText(this, R.string.delete_failed_images_message, Toast.LENGTH_SHORT).show();
        }
    }
}
