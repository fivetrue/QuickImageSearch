package com.fivetrue.app.imagequicksearch.ui.adapter.holder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.app.AppDB;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class AppInfoHolder extends RecyclerView.ViewHolder {

    public final View layout;
    public final ImageView image;
    public final TextView text;
    public final View favorite;

    public AppInfoHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.layout_item_app_info_list);
        image = (ImageView) itemView.findViewById(R.id.iv_item_app_info_list);
        text = (TextView) itemView.findViewById(R.id.tv_item_app_info_list);
        favorite = itemView.findViewById(R.id.iv_item_app_info_favorite);
    }

    public static AppInfoHolder makeHolder(Context context){
        return new AppInfoHolder(LayoutInflater.from(context).inflate(R.layout.item_app_info_list, null));
    }

    public void setAppInfo(ResolveInfo appInfo){
        PackageManager pm = image.getContext().getPackageManager();
        image.setImageDrawable(appInfo.loadIcon(pm));
        text.setText(appInfo.loadLabel(pm));
        if(AppDB.getInstance().isFavoriteApp(appInfo.activityInfo.packageName)){
            favorite.setVisibility(View.VISIBLE);
        }else{
            favorite.setVisibility(View.GONE);
        }
    }
}
