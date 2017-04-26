package com.fivetrue.app.imagequicksearch.model.app;

import io.realm.RealmObject;

/**
 * Created by kwonojin on 2017. 4. 26..
 */

public class AppInfo extends RealmObject{

    private String appName;
    private String packageName;
    private String targetClass;
    private long updateDate;
    private int sharedCount;
    private long sharedImageCount;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
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
}
