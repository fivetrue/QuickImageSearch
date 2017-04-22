package com.fivetrue.app.imagequicksearch.model.image;

import io.realm.RealmObject;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class CachedGoogleImage extends RealmObject {

    private int cr;
    private String id;

    private String mimeType;
    private String imageUrl;
    private int imageWidth;
    private int imageHeight;

    private String thumbnailUrl;
    private int thumbnailWidth;
    private int thumbnailHeight;

    private String siteUrl;
    private String siteTitle;

    private String pageUrl;
    private String pageTitle;

    private String pageText;

    private String rid;

    private long updateDate;

    private String keyword;

    public int getCr() {
        return cr;
    }

    public void setCr(int cr) {
        this.cr = cr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getThumbnailWidth() {
        return thumbnailWidth;
    }

    public void setThumbnailWidth(int thumbnailWidth) {
        this.thumbnailWidth = thumbnailWidth;
    }

    public int getThumbnailHeight() {
        return thumbnailHeight;
    }

    public void setThumbnailHeight(int thumbnailHeight) {
        this.thumbnailHeight = thumbnailHeight;
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

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageText() {
        return pageText;
    }

    public void setPageText(String pageText) {
        this.pageText = pageText;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }
}
