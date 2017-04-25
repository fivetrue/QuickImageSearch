package com.fivetrue.app.imagequicksearch.model.image;

import io.realm.RealmObject;

/**
 * Created by kwonojin on 2017. 4. 19..
 */

public class LikeImage extends RealmObject {


    private String imageUrl;
    private long updateDate;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

}
