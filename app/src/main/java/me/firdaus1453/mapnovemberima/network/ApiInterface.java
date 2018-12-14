package me.firdaus1453.mapnovemberima.network;

import me.firdaus1453.mapnovemberima.model.ResponseRoute;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("api/directions/json")
    Call<ResponseRoute> request_route(@Query("origin") String origin,
                                      @Query("destination") String destination,
                                      @Query("key") String key);

}
