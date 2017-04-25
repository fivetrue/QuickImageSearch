package com.fivetrue.app.imagequicksearch.ui.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.fivetrue.app.imagequicksearch.R;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class FooterHolder extends RecyclerView.ViewHolder {

    public ProgressBar progressBar;

    public FooterHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.pb_footer);
    }

    public static FooterHolder makeHolder(Context context){
        return new FooterHolder(LayoutInflater.from(context).inflate(R.layout.item_list_footer, null));
    }
}
