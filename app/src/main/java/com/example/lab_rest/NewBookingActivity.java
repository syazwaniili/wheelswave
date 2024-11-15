package com.example.lab_rest;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewBookingActivity extends AppCompatActivity {

    private BookingService bookingService;
    private EditText etPickupDate, etReturnDate, etPickupLocation, etReturnLocation;
    private Button btnSubmitBooking, btnPickupDate, btnReturnDate;
    private double carPrice;
    private int carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etPickupDate = findViewById(R.id.etPickupDate);
        etReturnDate = findViewById(R.id.etReturnDate);
        etPickupLocation = findViewById(R.id.etPickupLocation);
        etReturnLocation = findViewById(R.id.etReturnLocation);
        btnSubmitBooking = findViewById(R.id.btnSubmitBooking);
        btnPickupDate = findViewById(R.id.btnPickupDate);
        btnReturnDate = findViewById(R.id.btnReturnDate);

        Intent intent = getIntent();
        carPrice = intent.getDoubleExtra("car_price", 0);
        carId = intent.getIntExtra("car_id", -1);

        btnSubmitBooking.setOnClickListener(v -> addNewBooking());

        btnPickupDate.setOnClickListener(this::showDatePickerDialog);
        btnReturnDate.setOnClickListener(this::showDatePickerDialog);
    }

    public void addNewBooking() {
        Log.d("NewBookingActivity", "addNewBooking called");

        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        String pickupDate = etPickupDate.getText().toString();
        String returnDate = etReturnDate.getText().toString();

        long diff = 0;
        try {
            diff = new SimpleDateFormat("yyyy-MM-dd").parse(returnDate).getTime() -
                    new SimpleDateFormat("yyyy-MM-dd").parse(pickupDate).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Invalid date format. Use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        long diffDays = diff / (24 * 60 * 60 * 1000);
        double totalPrice = diffDays * carPrice;

        bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.addBooking(
                token,
                pickupDate,
                returnDate,
                etPickupLocation.getText().toString(),
                etReturnLocation.getText().toString(),
                "New",
                totalPrice,
                user.getId(),
                carId
        );

        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                Log.d("NewBookingActivity", "onResponse called");
                if (response.isSuccessful()) {
                    Log.d("NewBookingActivity", "Booking added successfully");
                    Toast.makeText(getApplicationContext(), "Booking added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), CustomerBookingListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("NewBookingActivity", "Error: " + response.message() + " Code: " + response.code());
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Log.e("NewBookingActivity", "Error connecting to server: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDatePickerDialog(View view) {
        final String tag = (String) view.getTag();
        final EditText editText;

        if ("pickup_date".equals(tag)) {
            editText = etPickupDate;
        } else if ("return_date".equals(tag)) {
            editText = etReturnDate;
        } else {
            return;
        }

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, year1, monthOfYear, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
            editText.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }
}
