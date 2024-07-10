package com.example.lab_rest.remote;

import com.example.lab_rest.model.Car;
import com.example.lab_rest.model.DeleteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarService {

    @GET("car/?order=manufacturer&orderType=asc")
    Call<List<Car>> getAllCars(@Header("api-key") String api_key);

    @GET("car/{id}")
    Call<Car> getCar(@Header("api-key") String api_key, @Path("id") int id);

    @FormUrlEncoded
    @POST("car")
    Call<Car> addCar(@Header ("api-key") String apiKey,
                     @Field("category") String category,
                     @Field("seats") String seats,
                     @Field("price") double price,
                     @Field("mileage") int mileage,
                     @Field("manufacturer") String manufacturer,
                     @Field("model") String model,
                     @Field("year") String year,
                     @Field("image") String image,
                     @Field("status") String status);

    @DELETE("car/{id}")
    Call<DeleteResponse> deleteCar(@Header ("api-key") String apiKey, @Path("id") int id);

    @FormUrlEncoded
    @POST("car/{id}")
    Call<Car> updateCar(@Header ("api-key") String apiKey,
                        @Field("category") String category,
                        @Field("seats") String seats,
                        @Field("price") double price,
                        @Field("mileage") int mileage,
                        @Field("manufacturer") String manufacturer,
                        @Field("model") String model,
                        @Field("year") String year,
                        @Field("image") String image,
                        @Field("status") String status);
}