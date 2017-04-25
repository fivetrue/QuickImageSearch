package com.fivetrue.app.imagequicksearch.ui.adapter.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.fivetrue.app.imagequicksearch.R;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class HeaderHolder extends RecyclerView.ViewHolder {

    public HeaderHolder(View itemView) {
        super(itemView);
    }

    public static HeaderHolder makeHolder(Context context){
        return new HeaderHolder(LayoutInflater.from(context).inflate(R.layout.item_list_header, null));
    }
}
