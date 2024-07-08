package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lab_rest.adapter.CarAdapter;
import com.example.lab_rest.model.Car;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.CarService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarListActivity extends AppCompatActivity {

    private CarService carService;
    private RecyclerView rvCarList;
    private CarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        rvCarList = findViewById(R.id.rvCarList);
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        carService = ApiUtils.getCarService();

        carService.getAllCars(token).enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Car> cars = response.body();
                    adapter = new CarAdapter(getApplicationContext(), cars);
                    rvCarList.setAdapter(adapter);
                    rvCarList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvCarList.addItemDecoration(new DividerItemDecoration(rvCarList.getContext(), DividerItemDecoration.VERTICAL));
                } else {
                    Toast.makeText(getApplicationContext(), "Error fetching cars", Toast.LENGTH_LONG).show();
                    if (response.code() == 401) {
                        // Invalid token, ask user to relogin
                        Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        startActivity(new Intent(CarListActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_LONG).show();
            }
        });
    }
}
