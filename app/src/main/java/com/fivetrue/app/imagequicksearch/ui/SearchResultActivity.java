package com.fivetrue.app.imagequicksearch.ui;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

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

}
