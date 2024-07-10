package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_rest.model.Booking;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookingService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewBookingActivity extends AppCompatActivity {

    private BookingService bookingService;
    private EditText etPickupDate, etReturnDate, etPickupLocation, etReturnLocation, etBookingStatus, etTotalPrice;
    private Button btnSubmitBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);

        // Get references to UI elements
        etPickupDate = findViewById(R.id.etPickupDate);
        etReturnDate = findViewById(R.id.etReturnDate);
        etPickupLocation = findViewById(R.id.etPickupLocation);
        etReturnLocation = findViewById(R.id.etReturnLocation);
        etBookingStatus = findViewById(R.id.etBookingStatus);
        etTotalPrice = findViewById(R.id.etTotalPrice);
        btnSubmitBooking = findViewById(R.id.btnSubmitBooking);

        btnSubmitBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBooking();
            }
        });

        Intent intent = getIntent();
        int carId = intent.getIntExtra("car_id", -1);
        // Use carId to set specific car details or pass it to the booking service

    }

    private void addNewBooking() {
        // Retrieve user info
        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        // Convert strings to Date objects
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date pickupDate = null;
        Date returnDate = null;
        try {
            pickupDate = sdf.parse(etPickupDate.getText().toString());
            returnDate = sdf.parse(etReturnDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Invalid date format. Use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new booking object
        Booking newBooking = new Booking();
        newBooking.setPickup_date(pickupDate);
        newBooking.setReturn_date(returnDate);
        newBooking.setPickup_location(etPickupLocation.getText().toString());
        newBooking.setReturn_location(etReturnLocation.getText().toString());
        newBooking.setBooking_status("new");
        newBooking.setTotalPrice(Double.parseDouble(etTotalPrice.getText().toString()));
        newBooking.setUser_id(user.getId());
        newBooking.setCar_id(getIntent().getIntExtra("car_id", -1)); // Set the car ID

        bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.addBooking(token, newBooking.getBookingID(), newBooking.getPickup_date(), newBooking.getReturn_date(),
                newBooking.getPickup_location(), newBooking.getReturn_location(), newBooking.getBooking_status(), newBooking.getTotalPrice(),
                newBooking.getUser_id(), newBooking.getAdmin_id(), newBooking.getCar_id());

        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Booking added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewBookingActivity.this, BookingListActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
