package com.fivetrue.app.imagequicksearch.ui.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class RetrievedImageHolder extends RecyclerView.ViewHolder {

    public final View layout;
    public final ImageView image;
    public final TextView text;
    public final View check;

    public RetrievedImageHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.layout_item_cached_image_list);
        image = (ImageView) itemView.findViewById(R.id.iv_item_cached_image_list);
        text = (TextView) itemView.findViewById(R.id.tv_item_cached_image_list);
        check = itemView.findViewById(R.id.iv_item_cached_image_list_check);
    }

    public static RetrievedImageHolder makeHolder(Context context){
        return new RetrievedImageHolder(LayoutInflater.from(context).inflate(R.layout.item_cached_image_list, null));
    }

    public void setImage(CachedGoogleImage image){
        Glide.with(this.image.getContext()).load(image.getThumbnailUrl()).into(this.image);
        text.setText(image.getKeyword());
    }
}
