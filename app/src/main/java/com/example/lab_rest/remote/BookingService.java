package com.example.lab_rest.remote;
import com.example.lab_rest.model.Car;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.Booking;


import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingService {
    @GET("booking/?order=pickup_date&orderType=asc")
    Call<List<Booking>> getAllBooking(@Header("api-key") String api_key);

    @GET("booking/{bookingID}")
    Call<Booking> getBooking(@Header("api-key") String api_key, @Path("bookingID") int bookingID);

    @FormUrlEncoded
    @POST("booking")
    Call<Booking> addBooking(@Header ("api-key") String apiKey,
                         @Field("bookingID") int bookingID,
                         @Field("pickup_date") Date pickup_date,
                         @Field("return_date") Date return_date,
                         @Field("pickup_location") String pickup_location,
                         @Field("return_location") String return_location,
                         @Field("booking_status") String booking_status,
                         @Field("totalPrice") double totalPrice,
                         @Field("user_id") int user_id,
                         @Field("admin_id") int admin_id,
                         @Field("car_id") int car_id);

    @DELETE("booking/{bookingID}")
    Call<DeleteResponse> deleteBooking(@Header ("api-key") String apiKey, @Path("bookingID") int bookingID);

    @FormUrlEncoded
    @POST("booking/{bookingID}")
    Call<Car> updateBooking(@Header ("api-key") String apiKey,
                            @Field("bookingID") int bookingID,
                            @Field("pickup_date") Date pickup_date,
                            @Field("return_date") Date seats,
                            @Field("pickup_location") String pickup_location,
                            @Field("return_location") String return_location,
                            @Field("booking_status") String booking_status,
                            @Field("totalPrice") double totalPrice,
                            @Field("user_id") int user_id,
                            @Field("admin_id") int admin_id,
                            @Field("car_id") int car_id);
}
