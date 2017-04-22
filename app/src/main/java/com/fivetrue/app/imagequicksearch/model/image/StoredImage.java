package com.fivetrue.app.imagequicksearch.model.image;

import io.realm.RealmObject;

/**
 * Created by kwonojin on 2017. 4. 21..
 */

public class StoredImage extends RealmObject{

    private String filePath;
    private String mimeType;
    private String imageUrl;
    private int imageWidth;
    private int imageHeight;

    private String siteUrl;
    private String siteTitle;

    private String keyword;

    private long storedDate;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSiteTitle() {
        return siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getStoredDate() {
        return storedDate;
    }

    public void setStoredDate(long storedDate) {
        this.storedDate = storedDate;
    }
}
