package com.example.lab_rest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_rest.adapter.BookingAdapter;
import com.example.lab_rest.model.Booking;
import com.example.lab_rest.model.DeleteResponse;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.BookingService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingListActivity extends AppCompatActivity {

    private BookingService bookingService;
    private RecyclerView rvBookingList;
    private BookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get reference to the RecyclerView BookingList
        rvBookingList = findViewById(R.id.rvBookingList);
        registerForContextMenu(rvBookingList);

        // fetch and update car list
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();
        String token = user.getToken();

        bookingService = ApiUtils.getBookingService(); //get booking service instance

        bookingService.getAllBooking(token).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //get list of booking from response
                    List<Booking> bookings = response.body();
                    //initialize adapter
                    adapter = new BookingAdapter(getApplicationContext(), bookings, true);
                    //set adapter to RV
                    rvBookingList.setAdapter(adapter);
                    //set layout to rv
                    rvBookingList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvBookingList.addItemDecoration(new DividerItemDecoration(rvBookingList.getContext(), DividerItemDecoration.VERTICAL));
                } else {
                    Toast.makeText(getApplicationContext(), "Error fetching booking of cars", Toast.LENGTH_LONG).show();
                    if (response.code() == 401) {
                        // Invalid token, ask user to re-login
                        Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                        SharedPrefManager.getInstance(getApplicationContext()).logout();
                        startActivity(new Intent(BookingListActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }


    /**
     * Delete booking record. Called by contextual menu "Delete"
     * @param selectedBooking - booking selected by user
     */
    private void doDeleteBooking(Booking selectedBooking) {
        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // prepare REST API call
        BookingService bookingService = ApiUtils.getBookingService();
        Call<DeleteResponse> call = bookingService.deleteBooking(user.getToken(), selectedBooking.getBookingID());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Booking of a car successfully deleted");
                    // update data in list view
                    updateRecyclerView();
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("MyApp: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }


    private void updateBookingStatus(Booking selectedBooking, String status, String message) {
        SharedPrefManager spm = SharedPrefManager.getInstance(getApplicationContext());
        User user = spm.getUser();

        BookingService bookingService = ApiUtils.getBookingService();
        Call<Booking> call = bookingService.updateBookingStatus(user.getToken(), selectedBooking.getBookingID(), status, message);

        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    displayAlert("Booking status updated successfully");
                    updateRecyclerView();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    if (response.code() == 401) {
                        Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                        clearSessionAndRedirect();
                    }
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
            }
        });
    }

    private void showUpdateStatusDialog(Booking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Booking Status");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Enter message");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = input.getText().toString();
                updateBookingStatus(booking, "approved", message);
            }
        });
        builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = input.getText().toString();
                updateBookingStatus(booking, "rejected", message);
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.booking_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Booking selectedBooking = adapter.getSelectedItem();
        Log.d("MyApp", "selected " + selectedBooking.toString());

        //user clicked details contextual menu
        if (item.getItemId() == R.id.menu_details) {
            doViewDetails(selectedBooking);
        }
        else if (item.getItemId() == R.id.menu_delete) {
            // user clicked the delete contextual menu
            doDeleteBooking(selectedBooking);
        }
        else if (item.getItemId() == R.id.menu_update_status) {
            showUpdateStatusDialog(selectedBooking);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    /*private void doUpdateCar(Booking selectedBooking) {
        Log.d("My App:", "update car: " + selectedBooking.toString());
        //forward user to UpdateBookingActivity, passing the selected car id
        Intent intent = new Intent(getApplicationContext(), UpdateBookingActivity.class);
        intent.putExtra("bookingID", selectedBooking.getBookingID());
        startActivity(intent);
    }*/

    private void doViewDetails(Booking selectedBooking) {
        Log.d("MyApp:", "viewing details: " + selectedBooking.toString());
        // forward user to CarDetailsActivity, passing the selected car id
        Intent intent = new Intent(getApplicationContext(), BookingDetailsActivity.class);
        intent.putExtra("bookingID", selectedBooking.getBookingID());
        startActivity(intent);
    }

    public void floatingAddCarClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), NewCarActivity.class);
        startActivity(intent);
    }
}


