package com.example.lab_rest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.CarAdapter;
import com.example.lab_rest.model.Car;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.CarService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCarListActivity extends AppCompatActivity {

    private CarService carService;
    private RecyclerView rvCustomerCarList;
    private CarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.customer_activity_car_list);

        rvCustomerCarList = findViewById(R.id.rvCustomerCarList);
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        carService = ApiUtils.getCarService();

        carService.getAllCars(token).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Car> cars = response.body();
                    adapter = new CarAdapter(getApplicationContext(), cars);
                    rvCustomerCarList.setAdapter(adapter);
                    rvCustomerCarList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvCustomerCarList.addItemDecoration(new DividerItemDecoration(rvCustomerCarList.getContext(), DividerItemDecoration.VERTICAL));
                } else if (response.code() == 401) {
                    //invalid toke, user need to re-login
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to the server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    private void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate mainActivity
        finish();

        // forward to login page
        Intent intent = new Intent(CustomerCarListActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
