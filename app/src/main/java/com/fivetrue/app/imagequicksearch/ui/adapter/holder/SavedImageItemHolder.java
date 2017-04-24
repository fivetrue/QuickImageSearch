package com.fivetrue.app.imagequicksearch.ui.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class SavedImageItemHolder extends RecyclerView.ViewHolder {

    public final View layout;
    public final ImageView image;
    public final ImageView check;

    public SavedImageItemHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.layout_item_image_list);
        image = (ImageView) itemView.findViewById(R.id.iv_item_image_list);
        check = (ImageView) itemView.findViewById(R.id.iv_item_image_list_check);
    }

    public static SavedImageItemHolder makeHolder(Context context){
        return new SavedImageItemHolder(LayoutInflater.from(context).inflate(R.layout.item_saved_image_list, null));
    }

    public void setImage(GoogleImage image){
        Glide.with(this.image.getContext()).load(image.getThumbnailUrl()).placeholder(R.drawable.ic_default_thumbnail_50dp).into(this.image);
    }
}
