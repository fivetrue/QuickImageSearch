package com.fivetrue.app.imagequicksearch.model.image;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by kwonojin on 2017. 2. 21..
 */


public class GoogleImage implements Parcelable{

    public int cr;
    public String id;
    public String isu;
    public boolean itg;
    public String ity;
    public int oh;
    public String ou;
    public int ow;
    public String pt;
    public String rid;
    public String ru;
    public String s;
    public String st;
    public String tu;
    public int th;
    public int tw;

    public GoogleImage(CachedGoogleImage image){
        cr = image.getCr();
        id = image.getId();
        isu = image.getSiteUrl();
        ru = image.getPageUrl();
        ity = image.getMimeType();
        ow = image.getImageWidth();
        oh = image.getImageHeight();
        ou = image.getImageUrl();
        pt = image.getPageText();
        s = image.getPageTitle();
        st = image.getSiteTitle();
        rid = image.getRid();
        tu = image.getThumbnailUrl();
        tw = image.getThumbnailWidth();
        th = image.getThumbnailHeight();
    }

    public GoogleImage(SavedImage image){
        isu = image.getSiteUrl();
        ity = image.getMimeType();
        ow = image.getImageWidth();
        oh = image.getImageHeight();
        ou = image.getImageUrl();
        st = image.getSiteTitle();
    }

    protected GoogleImage(Parcel in) {
        cr = in.readInt();
        id = in.readString();
        isu = in.readString();
        itg = in.readByte() != 0;
        ity = in.readString();
        oh = in.readInt();
        ou = in.readString();
        ow = in.readInt();
        pt = in.readString();
        rid = in.readString();
        ru = in.readString();
        s = in.readString();
        st = in.readString();
        tu = in.readString();
        th = in.readInt();
        tw = in.readInt();
    }

    public static final Creator<GoogleImage> CREATOR = new Creator<GoogleImage>() {
        @Override
        public GoogleImage createFromParcel(Parcel in) {
            return new GoogleImage(in);
        }

        @Override
        public GoogleImage[] newArray(int size) {
            return new GoogleImage[size];
        }
    };

    public String getId(){
        return id;
    }

    public String getSiteUrl(){
        return isu;
    }

    public String getPageUrl(){
        return ru;
    }

    public String getImageMimeType(){
        return ity;
    }

    public int getOriginalWidth(){
        return ow;
    }

    public int getOriginalHeight(){
        return oh;
    }

    public String getOriginalImageUrl(){
        return ou;
    }

    public String getPageText(){
        return pt;
    }

    public String getSubject(){
        return s;
    }

    public String getSiteTitle(){
        return st;
    }

    public String getRid(){
        return rid;
    }

    public String getThumbnailUrl(){
        return tu;
    }

    public int getThumbnailWidth(){
        return tw;
    }

    public int getThumbnailHeight(){
        return th;
    };

    @Override
    public String toString() {
        return "GoogleImage{" +
                "cr=" + cr +
                ", id='" + id + '\'' +
                ", isu='" + isu + '\'' +
                ", itg=" + itg +
                ", ity='" + ity + '\'' +
                ", oh=" + oh +
                ", ou='" + ou + '\'' +
                ", ow=" + ow +
                ", pt='" + pt + '\'' +
                ", rid='" + rid + '\'' +
                ", ru='" + ru + '\'' +
                ", s='" + s + '\'' +
                ", st='" + st + '\'' +
                ", th=" + th +
                ", tu='" + tu + '\'' +
                ", tw='" + tw + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(cr);
        parcel.writeString(id);
        parcel.writeString(isu);
        parcel.writeByte((byte) (itg ? 1 : 0));
        parcel.writeString(ity);
        parcel.writeInt(oh);
        parcel.writeString(ou);
        parcel.writeInt(ow);
        parcel.writeString(pt);
        parcel.writeString(rid);
        parcel.writeString(ru);
        parcel.writeString(s);
        parcel.writeString(st);
        parcel.writeString(tu);
        parcel.writeInt(th);
        parcel.writeInt(tw);
    }

    public CachedGoogleImage parseCachedImage(String q){
        CachedGoogleImage image = new CachedGoogleImage();
        image.setCr(cr);
        image.setId(id);
        image.setMimeType(ity);
        image.setImageUrl(ou);
        image.setImageHeight(oh);
        image.setImageWidth(ow);
        image.setThumbnailUrl(tu);
        image.setThumbnailWidth(tw);
        image.setThumbnailHeight(th);
        image.setSiteUrl(isu);
        image.setSiteTitle(st);
        image.setPageText(pt);
        image.setPageTitle(s);
        image.setPageUrl(ru);
        image.setRid(rid);
        image.setKeyword(q);
        image.setUpdateDate(System.currentTimeMillis());
        return image;
    }

    public SavedImage parseStoreImage(String q, File file){
        SavedImage image = new SavedImage();
        image.setMimeType(ity);
        image.setImageUrl(ou);
        image.setImageHeight(oh);
        image.setImageWidth(ow);
        image.setSiteUrl(isu);
        image.setSiteTitle(st);
        image.setPageTitle(s);
        image.setPageUrl(ru);
        image.setKeyword(q);
        image.setStoredDate(System.currentTimeMillis());
        image.setFilePath(file.getAbsolutePath());
        return image;
    }
}
