package com.fivetrue.app.imagequicksearch.model.app;

import io.realm.RealmObject;

/**
 * Created by kwonojin on 2017. 4. 26..
 */

public class AppInfo extends RealmObject{

    private String packageName;
    private String targetActivity;
    private int iconResource;
    private String name;
    private long updateDate;
    private int sharedCount;
    private long sharedImageCount;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public int getSharedCount() {
        return sharedCount;
    }

    public void setSharedCount(int sharedCount) {
        this.sharedCount = sharedCount;
    }

    public long getSharedImageCount() {
        return sharedImageCount;
    }

    public void setSharedImageCount(long sharedImageCount) {
        this.sharedImageCount = sharedImageCount;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
