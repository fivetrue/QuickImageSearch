package com.fivetrue.app.imagequicksearch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.SavedImage;

import java.io.File;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class ImageStoreUtil {

    private static final String TAG = "ImageStoreUtil";

    private Context mContext;

    private File mFileDir;

    private static ImageStoreUtil sInstance;

    public static ImageStoreUtil getInstance(Context context){
        if(sInstance == null){
            sInstance = new ImageStoreUtil(context.getApplicationContext());
        }
        return sInstance;
    }

    private ImageStoreUtil(Context context){
        mContext = context;
        mFileDir = context.getFilesDir();
        mFileDir.mkdirs();
    }

    public Observable<File> saveNetworkImage(GoogleImage image, String q){
        if(LL.D) Log.d(TAG, "saveNetworkImage() called with: imageUrl = [" + image + "]");
        SavedImage savedImage = ImageDB.getInstance().findSavedImage(image.getOriginalImageUrl());
        if(savedImage != null){
            if(LL.D)
                Log.d(TAG, "saveNetworkImage() has StoredImage");
            return Observable.just(savedImage).map(img -> new File(img.getFilePath()));
        }else{
            if(LL.D)
                Log.d(TAG, "saveNetworkImage() try to get network image");
            return Observable.create(emitter -> {
                if(LL.D) Log.d(TAG, "saveNetworkImage: try to load image");
                Glide.with(mContext).load(image.getOriginalImageUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if(LL.D) Log.d(TAG, "saveNetworkImage: received image");
                        if(LL.D) Log.d(TAG, "saveNetworkImage: try to save image");
                        Observable.just(resource)
                                .map(bitmap -> {
                                    File imageFile = new File(mFileDir, System.currentTimeMillis() + ".png");
                                    FileOutputStream out = new FileOutputStream(imageFile);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    out.close();
                                    if(LL.D) Log.d(TAG, "saveNetworkImage: save image path = " + imageFile.getAbsolutePath());
                                    return imageFile;
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(file ->{
                                    if(LL.D) Log.d(TAG, "saveNetworkImage: insert saved image to StoredImage");
                                    ImageDB.getInstance().insertSavedImage(image.parseStoreImage(q, file));
                                    emitter.onNext(file);
                                });
                    }
                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
//                        if(!failed){
//                            Glide.with(mContext).load(image.getThumbnailUrl()).asBitmap().into(this);
//                            failed = true;
//                        }
                        emitter.onError(e);
                    }
                });
            });
        }
    }


}
