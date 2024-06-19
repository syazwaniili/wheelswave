package com.example.lab_rest.remote;

import com.example.lab_rest.model.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface CarService {

    @GET("car/?order=manufacturer&orderType=asc")
    Call<List<Car>> getAllCars(@Header("api-key") String api_key);

    @GET("car/{id}")
    Call<Car> getCar(@Header("api-key") String api_key, @Path("id") int id);

}