package com.fivetrue.app.imagequicksearch.net.service;


import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by kwonojin on 2017. 2. 21..
 */


public interface GoogleImageService {

    @GET("search")
    Observable<GoogleImage> getGoogleImage(@QueryMap Map<String, String> query);

}