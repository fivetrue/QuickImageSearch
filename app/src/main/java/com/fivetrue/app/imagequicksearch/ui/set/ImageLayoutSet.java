package com.fivetrue.app.imagequicksearch.ui.set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fivetrue.app.imagequicksearch.R;

/**
 * Created by kwonojin on 2017. 4. 23..
 */

public class ImageLayoutSet extends LinearLayout {

    private static final String TAG = "ImageLayoutSet";

    private ImageView mIcon;
    private TextView mTitle;
    private View mMore;

    private RecyclerView mImageList;

    public ImageLayoutSet(Context context) {
        super(context);
        initView(context, null);
    }

    public ImageLayoutSet(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ImageLayoutSet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.image_layout_set, this);
        mIcon = (ImageView) findViewById(R.id.iv_image_layout_set);
        mTitle = (TextView) findViewById(R.id.tv_image_layout_set);
        mMore = findViewById(R.id.layout_image_layout_set_more);
        mMore.setVisibility(INVISIBLE);

        mImageList = (RecyclerView) findViewById(R.id.rv_image_layout_set);

        if(attributeSet != null){
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.ImageLayoutSet);
            Drawable drawable = a.getDrawable(R.styleable.ImageLayoutSet_icon);
            String title = a.getString(R.styleable.ImageLayoutSet_title);
            if(drawable != null){
                mIcon.setImageDrawable(drawable);
            }
            if(!TextUtils.isEmpty(title)){
                mTitle.setText(title);
            }
            a.recycle();
        }
    }

    public void setOnClickMoreListener(OnClickListener onClickMoreListener){
        mMore.setVisibility(VISIBLE);
        mMore.setOnClickListener(view -> {
            onClickMoreListener.onClick(ImageLayoutSet.this);
        });
    }

    public void setLayoutManager(LinearLayoutManager manager){
        mImageList.setLayoutManager(manager);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        mImageList.setAdapter(adapter);
    }
}
