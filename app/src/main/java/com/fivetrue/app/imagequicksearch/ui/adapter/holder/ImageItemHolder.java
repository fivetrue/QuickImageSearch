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

public class ImageItemHolder extends RecyclerView.ViewHolder {

    public final View layout;
    public final ImageView image;
    public final ImageView check;
    public final ImageView gif;

    public ImageItemHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.layout_item_image_list);
        image = (ImageView) itemView.findViewById(R.id.iv_item_image_list);
        check = (ImageView) itemView.findViewById(R.id.iv_item_image_list_check);
        gif = (ImageView) itemView.findViewById(R.id.iv_item_image_list_gif);
    }

    public static ImageItemHolder makeHolder(Context context){
        return new ImageItemHolder(LayoutInflater.from(context).inflate(R.layout.item_search_image_list, null));
    }

    public void setImage(GoogleImage image){
        Glide.with(this.image.getContext()).load(image.getThumbnailUrl()).into(this.image);
        gif.setVisibility(image.isGif() ? View.VISIBLE : View.GONE);
    }
}
