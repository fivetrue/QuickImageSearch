package com.fivetrue.app.imagequicksearch.database.image;

import android.content.Context;

import com.fivetrue.app.imagequicksearch.database.RealmDB;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.StoredImage;
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

    private PublishSubject<List<StoredImage>> mStoreImagePublishSubject = PublishSubject.create();

    private static ImageDB sInstance;

    public static void init(Context context){
        sInstance = new ImageDB(context.getApplicationContext());
    }

    public static ImageDB getInstance(){
        return sInstance;
    }

    private ImageDB(Context context){
        mContext = context;
        Observable.fromIterable(get().where(CachedGoogleImage.class).findAll())
                .filter(image -> image.getUpdateDate() + CACHE_PERIOD_TIME < System.currentTimeMillis())
                .toList().subscribe(cachedGoogleImages -> {

        }, throwable -> TrackingUtil.getInstance().report(throwable));
    }

    public List<CachedGoogleImage> findImages(String key, String value){
        return get().where(CachedGoogleImage.class).equalTo(key, value).findAll();
    }

    public List<CachedGoogleImage> containImages(String key, String value){
        return get().where(CachedGoogleImage.class).contains(key, value).findAll();
    }

    public void insertGoogleImage(CachedGoogleImage image){
        get().executeTransaction(realm -> get().insert(image));
    }

    public void insertGoogleImage(List<CachedGoogleImage> image){
        get().executeTransaction(realm -> get().insert(image));
    }

    public void insertStoredImage(StoredImage image){
        get().executeTransaction(realm -> get().insert(image));
    }

    public void insertStoredImage(List<StoredImage> images){
        get().executeTransaction(realm -> get().insert(images));

    }

    public List<StoredImage> getStoredImages(){
        return get().where(StoredImage.class).findAllSorted("storedDate", Sort.DESCENDING);
    }

    public StoredImage getStoreImageByUrl(String imageUrl){
        return get().where(StoredImage.class).equalTo("imageUrl", imageUrl).findFirst();
    }

    public StoredImage getStoreImage(GoogleImage image){
        return get().where(StoredImage.class).equalTo("imageUrl", image.getOriginalImageUrl()).findFirst();
    }

    @Override
    public void onChange(Realm element) {
        publishStoredImage();
    }

    public Observable<List<StoredImage>> getStoredImageObservable(){
        return mStoreImagePublishSubject;
    }

    public void publishStoredImage(){
        mStoreImagePublishSubject.onNext(getStoredImages());
        mStoreImagePublishSubject.publish();
    }
}
