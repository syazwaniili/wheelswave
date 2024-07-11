package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.model.Booking;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookingService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsActivity extends AppCompatActivity {

    private BookingService bookingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d("BookingDetailsActivity", "onCreate called");

        // Retrieve booking details based on selected id
        Intent intent = getIntent();
        int bookingID = intent.getIntExtra("bookingID", -1);
        Log.d("BookingDetailsActivity", "Received bookingID: " + bookingID);

        // Get user info from SharedPreferences
        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();
        Log.d("BookingDetailsActivity", "User token: " + token);

        // Get booking service instance
        bookingService = ApiUtils.getBookingService();

        // Execute the API query to get booking details
        bookingService.getBooking(token, bookingID).enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Booking booking = response.body();

                    // Populate UI with booking details
                    populateBookingDetails(booking);
                } else {
                    handleErrorResponse(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting", Toast.LENGTH_LONG).show();
                Log.e("BookingDetailsActivity", "Error: " + t.getMessage());
            }
        });
    }

    private void populateBookingDetails(Booking booking) {
        // Get references to the view elements
        TextView tvBookingID = findViewById(R.id.tvBookingID);
        TextView tvPickup_date = findViewById(R.id.tvPickup_date);
        TextView tvReturn_date = findViewById(R.id.tvReturn_date);
        TextView tvPickup_location = findViewById(R.id.tvPickup_location);
        TextView tvReturn_location = findViewById(R.id.tvReturn_location);
        TextView tvBookingstatus = findViewById(R.id.tvBookingstatus);
        TextView tvTotalprice = findViewById(R.id.tvTotalprice);
        TextView tvUser_id = findViewById(R.id.tvUser_id);
        TextView tvAdmin_id = findViewById(R.id.tvAdmin_id);
        TextView tvCar_id = findViewById(R.id.tvCar_id);

        // Set values
        tvBookingID.setText(String.valueOf(booking.getBookingID()));
        tvPickup_date.setText(booking.getPickup_date());
        tvReturn_date.setText(booking.getReturn_date());
        tvPickup_location.setText(booking.getPickup_location());
        tvReturn_location.setText(booking.getReturn_location());
        tvBookingstatus.setText(booking.getBooking_status());
        tvTotalprice.setText(String.valueOf(booking.getTotalPrice()));
        tvUser_id.setText(String.valueOf(booking.getUser_id()));
        tvAdmin_id.setText(String.valueOf(booking.getAdmin_id()));
        tvCar_id.setText(String.valueOf(booking.getCar_id()));
    }

    private void handleErrorResponse(int code, String message) {
        if (code == 401) {
            // Unauthorized error. Invalid token, ask user to relogin
            Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
            clearSessionAndRedirect();
        } else {
            // Server returned other error
            Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            Log.e("BookingDetailsActivity", "Error: " + message);
        }
    }

    public void clearSessionAndRedirect() {
        Log.d("BookingDetailsActivity", "Clearing session and redirecting to login");
        // Clear the shared preferences
        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        spm.logout();

        // Terminate this activity
        finish();

        // Forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
