package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.model.Car;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.CarService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarDetailsActivity extends AppCompatActivity {
    private CarService carService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_car_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d("CarDetailsActivity", "onCreate called");

        // Retrieve car details based on selected id
        Intent intent = getIntent();
        int carId = intent.getIntExtra("car_id", -1);
        Log.d("CarDetailsActivity", "Received car_id: " + carId);

        // Get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();
        Log.d("CarDetailsActivity", "User token: " + token);

        // Get car service instance
        carService = ApiUtils.getCarService();

        // Execute the API query to get car details
        carService.getCar(token, carId).enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {
                if (response.isSuccessful()) {
                    Car car = response.body();

                    // Populate UI with car details
                    populateCarDetails(car);

                    // Add new car
                    addNewCar(token);
                } else {
                    handleErrorResponse(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting", Toast.LENGTH_LONG).show();
                Log.e("CarDetailsActivity", "Error: " + t.getMessage());
            }
        });
    }

    private void populateCarDetails(Car car) {
        // Get references to the view elements
        TextView tvCategory = findViewById(R.id.tvCategory);
        TextView tvManufacturer = findViewById(R.id.tvManufacturer);
        TextView tvModel = findViewById(R.id.tvModel);
        TextView tvYear = findViewById(R.id.tvYear);
        TextView tvMileage = findViewById(R.id.tvMileage);
        TextView tvImage = findViewById(R.id.tvImage);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvSeats = findViewById(R.id.tvSeats);
        TextView tvPrice = findViewById(R.id.tvPrice);

        // Set values
        tvCategory.setText(car.getCategory());
        tvManufacturer.setText(car.getManufacturer());
        tvModel.setText(car.getModel());
        tvYear.setText(car.getYear());
        tvMileage.setText(String.valueOf(car.getMileage()));
        tvImage.setText(car.getImage());
        tvStatus.setText(car.getStatus());
        tvSeats.setText(car.getSeats());
        tvPrice.setText(String.valueOf(car.getPrice()));
    }

    private void addNewCar(String token) {
        String category = "Sedan";
        String seats = "4";
        double price = 25000.0;
        int mileage = 15000;
        String manufacturer = "Toyota";
        String model = "Supra";
        String year = "2019";
        String status = "Available";
        String image = "image_url";

        carService.addCar(token, category, seats, price, mileage, manufacturer, model, year, status, image)
                .enqueue(new Callback<Car>() {
                    @Override
                    public void onResponse(Call<Car> call, Response<Car> response) {
                        if (response.isSuccessful()) {
                            Car car = response.body();
                            // Handle the successful response
                            Log.d("CarDetailsActivity", "Car added successfully: " + car.getId());
                        } else {
                            // Handle the error response
                            handleErrorResponse(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Car> call, Throwable t) {
                        // Handle the failure response
                        Toast.makeText(getApplicationContext(), "Error adding car", Toast.LENGTH_LONG).show();
                        Log.e("CarDetailsActivity", "Error: " + t.getMessage());
                    }
                });
    }

    private void handleErrorResponse(int code, String message) {
        if (code == 401) {
            // Unauthorized error. Invalid token, ask user to relogin
            Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
            clearSessionAndRedirect();
        } else {
            // Server returned other error
            Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
            Log.e("CarDetailsActivity", "Error: " + message);
        }
    }

    public void clearSessionAndRedirect() {
        Log.d("CarDetailsActivity", "Clearing session and redirecting to login");
        // Clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // Terminate this activity
        finish();

        // Forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}