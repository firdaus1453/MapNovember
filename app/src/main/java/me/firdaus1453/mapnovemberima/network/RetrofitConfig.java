package me.firdaus1453.mapnovemberima.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfig {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/";

    public static Retrofit getRetrofit  (){
        return new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }
    
    public static ApiInterface getInstanceRetrofit(){
        return RetrofitConfig.getRetrofit().create(ApiInterface.class);
    }
}
