package com.fivetrue.app.imagequicksearch.net.service;

import com.fivetrue.app.imagequicksearch.model.dto.GeoLocation;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by kwonojin on 2017. 2. 21..
 */


public interface GoogleApiService {

    @POST("/geolocation/v1/geolocate")
    Observable<GeoLocation> getGeoLocation(@Query("key") String apiKey);
}