package com.example.lab_rest;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;

import com.example.lab_rest.model.Booking;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookingService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewBookingActivity extends AppCompatActivity {

    private BookingService bookingService;
    private EditText etPickupDate, etReturnDate, etPickupLocation, etReturnLocation;
    private Button btnSubmitBooking;
    private double carPrice;
    private int carId;

    @SuppressLint("WrongViewCast")
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

        Intent intent = getIntent();
        carPrice = intent.getDoubleExtra("car_price", 0);
        carId = intent.getIntExtra("car_id", -1);

        btnSubmitBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBooking();
            }
        });
    }

    public void addNewBooking() {
        Log.d("NewBookingActivity", "addNewBooking called");

        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date pickup_date = null;
        Date return_date = null;
        try {
            pickup_date = sdf.parse(etPickupDate.getText().toString());
            return_date = sdf.parse(etReturnDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Invalid date format. Use yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        long diff = return_date.getTime() - pickup_date.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        double totalPrice = diffDays * carPrice;

        Booking newBooking = new Booking();
        newBooking.setPickup_date(pickup_date);
        newBooking.setReturn_date(return_date);
        newBooking.setPickup_location(etPickupLocation.getText().toString());
        newBooking.setReturn_location(etReturnLocation.getText().toString());
        newBooking.setBooking_status("new");
        newBooking.setTotalPrice(totalPrice);
        newBooking.setUser_id(user.getId());
        newBooking.setCar_id(carId);

        bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.addBooking(token, sdf.format(pickup_date), sdf.format(return_date), etPickupLocation.getText().toString(), etReturnLocation.getText().toString(), "new", totalPrice, newBooking.getUser_id(), newBooking.getAdmin_id(), carId);

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
                    Log.e("NewBookingActivity", "Error: " + response.message());
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

    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    public void showDatePickerDialog(View view) {
        // Determine which button was clicked by using the tag
        final String tag = (String) view.getTag();
        final EditText editText;

        if ("pickup_date".equals(tag)) {
            editText = etPickupDate;
        } else if ("return_date".equals(tag)) {
            editText = etReturnDate;
        } else {
            return;
        }

        // Get the current date to set as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new DatePickerDialog instance
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Format the date as yyyy-MM-dd and set it on the EditText
                String selectedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                editText.setText(selectedDate);
            }
        }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}
