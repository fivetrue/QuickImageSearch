package com.fivetrue.app.imagequicksearch.database.image;

import android.content.Context;
import android.text.TextUtils;

import com.fivetrue.app.imagequicksearch.database.RealmDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.Sort;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class ImageDB extends RealmDB implements RealmChangeListener<Realm>{

    private static final String TAG = "ImageDB";

    private static final long CACHE_PERIOD_TIME = 1000 * 60 * 60 * 24 * 7;

    private Context mContext;

    private PublishSubject<List<SavedImage>> mSavedImagePublishSubject = PublishSubject.create();
    private PublishSubject<List<CachedGoogleImage>> mCachedImagePublishSubject = PublishSubject.create();

    private static ImageDB sInstance;

    public static void init(Context context){
        sInstance = new ImageDB(context.getApplicationContext());
    }

    public static ImageDB getInstance(){
        return sInstance;
    }

    private ImageDB(Context context){
        mContext = context;
//        Observable.fromIterable(get().where(CachedGoogleImage.class).findAll())
//                .filter(image -> image.getUpdateDate() + CACHE_PERIOD_TIME < System.currentTimeMillis())
//                .toList().subscribe(cachedGoogleImages -> {
//
//        }, throwable -> TrackingUtil.getInstance().report(throwable));
        get().addChangeListener(this);

    }

    public List<CachedGoogleImage> findCachedImagesByKeyword(String value, Sort sort){
        return get().where(CachedGoogleImage.class).equalTo("keyword", value).findAllSorted("updateDate", sort);
    }

    public List<CachedGoogleImage> findCachedImages(String q){
        if(TextUtils.isEmpty(q)){
            return getCachedImages();
        }else{
            return get().where(CachedGoogleImage.class)
                    .contains("siteUrl", q).or()
                    .contains("siteTitle", q).or()
                    .contains("pageUrl", q).or()
                    .contains("pageTitle", q).or()
                    .contains("pageText", q).or()
                    .contains("keyword", q).or()
                    .findAll();
        }
    }

    public void insertGoogleImage(CachedGoogleImage image){
        get().executeTransaction(realm -> get().insert(image));
    }


    public void insertSavedImage(SavedImage image){
        get().executeTransaction(realm -> get().insert(image));
    }

    public CachedGoogleImage findCachedImage(String imageUrl){
        return get().where(CachedGoogleImage.class).equalTo("imageUrl", imageUrl).findFirst();
    }

    public List<CachedGoogleImage> getCachedImages(){
        return get().where(CachedGoogleImage.class).findAllSorted("updateDate", Sort.DESCENDING);
    }

    public List<CachedGoogleImage> getCachedImages(String keyword){
        return get().where(CachedGoogleImage.class).equalTo("keyword", keyword).findAll();
    }

    public void deleteCachedImages(CachedGoogleImage image){
        get().executeTransaction(realm -> {
            get().where(CachedGoogleImage.class).equalTo("keyword", image.getKeyword()).findAll().deleteAllFromRealm();
        });
    }

    public void deleteCachedImages(String keyword){
        get().executeTransaction(realm -> {
            get().where(CachedGoogleImage.class).equalTo("keyword", keyword).findAll().deleteAllFromRealm();
        });
    }

    public List<SavedImage> getSavedImages(){
        return get().where(SavedImage.class).findAllSorted("storedDate", Sort.DESCENDING);
    }

    public SavedImage findSavedImage(String imageUrl){
        return get().where(SavedImage.class).equalTo("imageUrl", imageUrl).findFirst();
    }

    public List<SavedImage> findSavedImages(String q){
        if(TextUtils.isEmpty(q)){
            return getSavedImages();
        }else{
            return get().where(SavedImage.class)
                    .contains("siteUrl", q)
                    .or()
                    .contains("siteTitle", q)
                    .or()
                    .contains("pageUrl", q)
                    .or()
                    .contains("pageTitle", q)
                    .or()
                    .contains("keyword", q)
                    .or()
                    .findAll();
        }
    }

    public void deleteSavedImages(List<SavedImage> images){
        get().executeTransaction(realm -> {
            for(SavedImage image : images){
                image.deleteFromRealm();
            }
        });
    }

    @Override
    public void onChange(Realm element) {
        publishSavedImage();
        publishCachedImage();
    }

    public Observable<List<SavedImage>> getSavedImageObservable(){
        return mSavedImagePublishSubject;
    }

    public Observable<List<CachedGoogleImage>> getCachedImageObservable(){
        return mCachedImagePublishSubject;
    }

    public void publishSavedImage(){
        mSavedImagePublishSubject.onNext(getSavedImages());
        mSavedImagePublishSubject.publish();
    }

    public void publishCachedImage(){
        mCachedImagePublishSubject.onNext(getCachedImages());
        mCachedImagePublishSubject.publish();
    }

}
