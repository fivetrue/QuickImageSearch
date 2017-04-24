package com.fivetrue.app.imagequicksearch.utils;

import android.content.Context;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.database.image.ImageDB;
import com.fivetrue.app.imagequicksearch.model.dto.GeoLocation;
import com.fivetrue.app.imagequicksearch.model.image.CachedGoogleImage;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.net.NetworkServiceProvider;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by kwonojin on 2017. 2. 23..
 */

public class DataManager {

    private static final String TAG = "DataManager";

    private static final Map<String, Observable> sObservableMap = new HashMap<>();

    private Context mContext;

    private static DataManager sInstance;

    public static DataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataManager(context);
        }
        return sInstance;
    }

    private DataManager(Context context) {
        mContext = context;
    }

    public Observable<GeoLocation> getGeoLocation() {
        return NetworkServiceProvider.getInstance()
                .getGoogleApiService().getGeoLocation(mContext.getString(R.string.image_google_api_key))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());
    }

    public Observable<List<GoogleImage>> findImage(String q) {
        TrackingUtil.getInstance().findImage(q);
        List<CachedGoogleImage> cachedImages = ImageDB.getInstance().findImages("keyword", q);
        if(cachedImages != null && cachedImages.size() > 0){
            return Observable.fromIterable(cachedImages)
                    .map(image-> new GoogleImage(image))
                    .toList().toObservable();

        }else{
            return Observable.create((ObservableOnSubscribe<List<GoogleImage>>) e -> {
                if(LL.D) Log.d(TAG, "findImage: q = " + q);
                try {
                    String googleUrl = "https://www.google.co.kr/search?tbm=isch&q=" + q + "&gws_rd=cr&ei=k0PyWKPiLoOC8wXFr4uoCg";
                    if(LL.D) Log.d(TAG, "findImage: googleUrl = " + googleUrl);
                    Document doc = Jsoup.connect(googleUrl).timeout(10 * 1000).get();
                    Elements elements = doc.select("div.rg_meta");
                    Observable.fromIterable(elements)
                            .map(element-> {
                                String json = element.childNode(0).toString().trim();
                                GoogleImage image = new Gson().fromJson(json, GoogleImage.class);
                                ImageDB.getInstance().insertGoogleImage(image.parseCachedImage(q));
                                return image;
                            }).toList().subscribe(images -> {
                        e.onNext(images);
                    });

                } catch (Exception e1) {
                    Log.e(TAG, "findImage: ", e1);
                    e.onError(e1);
                }
            }).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread());
        }
    }
}
