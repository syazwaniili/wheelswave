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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
        setContentView(R.layout.customer_activity_car_list);

        rvCustomerCarList = findViewById(R.id.rvCustomerCarList);
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
                    adapter = new CarAdapter(getApplicationContext(), cars, false);
                    rvCustomerCarList.setAdapter(adapter);
                    rvCustomerCarList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvCustomerCarList.addItemDecoration(new DividerItemDecoration(rvCustomerCarList.getContext(), DividerItemDecoration.VERTICAL));
                } else {
                    Toast.makeText(getApplicationContext(), "Error fetching cars", Toast.LENGTH_LONG).show();
                    if (response.code() == 401) {
                        // Invalid token, ask user to relogin
                        Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        startActivity(new Intent(CustomerCarListActivity.this, LoginActivity.class));
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

    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        //terminate mainActivity
        //finish();

        //forward to login page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflatercust = getMenuInflater();
        inflatercust.inflate(R.menu.customer_car_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Car selectedCar = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedCar.toString());

        //user clicked details contextual menu
        if (item.getItemId() == R.id.menu_details) {
            doViewDetails(selectedCar);
        }

        return super.onContextItemSelected(item);
    }

    private void doViewDetails(Car selectedCar) {
        Log.d("MyApp:", "viewing details: " + selectedCar.toString());
        Intent intent = new Intent(getApplicationContext(), CarDetailsActivity.class);
        intent.putExtra("car_id", selectedCar.getId());
        intent.putExtra("car_price", selectedCar.getPrice()); // Pass car price
        startActivity(intent);
    }

    public void floatingAddBookingClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), NewBookingActivity.class);
        startActivity(intent);
    }
}
