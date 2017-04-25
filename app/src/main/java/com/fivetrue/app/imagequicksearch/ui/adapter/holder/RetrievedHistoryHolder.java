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
import com.fivetrue.app.imagequicksearch.utils.CommonUtils;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class RetrievedHistoryHolder extends RecyclerView.ViewHolder {

    public final View layout;
    public final ImageView image;
    public final TextView text;
    public final TextView subText;
    public final View check;

    public RetrievedHistoryHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.layout_item_retrieved_history_list);
        image = (ImageView) itemView.findViewById(R.id.iv_item_retrieved_history_list);
        text = (TextView) itemView.findViewById(R.id.tv_item_retrieved_history_list_keyword);
        subText = (TextView) itemView.findViewById(R.id.tv_item_retrieved_history_list_date);
        check = itemView.findViewById(R.id.iv_item_retrieved_history_list_check);
    }

    public static RetrievedHistoryHolder makeHolder(Context context){
        return new RetrievedHistoryHolder(LayoutInflater.from(context).inflate(R.layout.item_retreived_history_list, null));
    }

    public void setImage(CachedGoogleImage image){
        Glide.with(this.image.getContext()).load(image.getThumbnailUrl()).into(this.image);
        text.setText(image.getKeyword());
        subText.setText(CommonUtils.getDate(subText.getContext(), "yyyy-MM-dd hh:mm", image.getUpdateDate()));
    }
}
