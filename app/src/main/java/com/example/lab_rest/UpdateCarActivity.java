package com.example.lab_rest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.model.Car;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.CarService;
import com.example.lab_rest.sharedpref.SharedPrefManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCarActivity extends AppCompatActivity {

    // form fields
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewCar;
    private EditText txtCategory;
    private EditText txtSeats;
    private EditText txtPrice;
    private EditText txtMileage;
    private EditText txtManufacturer;
    private EditText txtModel;
    private EditText txtYear;
    private Uri imageUri;
    private Car car; //current car to be updated

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_car);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get view objects references
        txtCategory = findViewById(R.id.txtCategory);
        txtSeats = findViewById(R.id.txtSeats);
        txtPrice = findViewById(R.id.txtPrice);
        txtMileage = findViewById(R.id.txtMileage);
        txtManufacturer = findViewById(R.id.txtManufacturer);
        txtModel = findViewById(R.id.txtModel);
        txtYear = findViewById(R.id.txtYear);

        // Initialize ImageView and Button
        imageViewCar = findViewById(R.id.imageViewCar);
        Button buttonSelectImage = findViewById(R.id.btnSelectImage);

        // Set OnClickListener for the button to open the image picker
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewCar.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateNewCar(View v) {
        // get values in form
        String category = txtCategory.getText().toString();
        String seats = txtSeats.getText().toString();
        String manufacturer = txtManufacturer.getText().toString();
        String model = txtModel.getText().toString();
        String year = txtYear.getText().toString();
        double price = Double.parseDouble(txtPrice.getText().toString());
        int mileage = Integer.parseInt(txtMileage.getText().toString());

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // Check if an image is selected
        String imageName = "default.jpg"; // default image name
        if (imageUri != null) {
            imageName = getFileName(imageUri); // Get the name of the selected image
            // Upload image to server if required, or use imageUri to include in request
        }

        // send request to add new car to the REST API
        CarService carService = ApiUtils.getCarService();
        Call<Car> call = carService.updateCar(user.getToken(), category, seats,  price, mileage, manufacturer,  model,
                year, imageName, "available");

        // execute
        call.enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                if (response.code() == 201) {
                    // car added successfully
                    Car addedCar = response.body();
                    // display message
                    Toast.makeText(getApplicationContext(),
                            addedCar.getManufacturer() + " added successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to CarListActivity
                    Intent intent = new Intent(getApplicationContext(), CarListActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (response.code() == 401) {
                    // invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    // server return other error
                    Log.e("NewCarActivity: ", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error [" + t.getMessage() + "]",
                        Toast.LENGTH_LONG).show();
                // for debug purpose
                Log.d("NewCarActivity", "Error: " + t.getCause().getMessage());
            }
        });
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private void clearSessionAndRedirect() {
        // clear the shared preferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();

        // terminate this MainActivity
        finish();

        // forward to Login Page
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Update book info in database when the user click Update Book button
     * @param view
     */
    public void updateBook(View view) {
        // get values in form
        String category = txtCategory.getText().toString();
        String seats = txtSeats.getText().toString();
        String manufacturer = txtManufacturer.getText().toString();
        String model = txtModel.getText().toString();
        String year = txtYear.getText().toString();
        double price = Double.parseDouble(txtPrice.getText().toString());
        int mileage = Integer.parseInt(txtMileage.getText().toString());


        Log.d("MyApp:", "Old Car info: " + car.toString());

        // update the book object retrieved in when populating the form with the new data.
        // update all fields excluding the id
        car.setCategory(category);
        car.setSeats(seats);
        car.setManufacturer(manufacturer);
        car.setModel(model);
        car.setYear(year);
        car.setPrice(price);
        car.setMileage(mileage);

        Log.d("MyApp:", "New Book info: " + car.toString());

        // get user info from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();

        // Check if an image is selected
        String imageName = "default.jpg"; // default image name
        if (imageUri != null) {
            imageName = getFileName(imageUri); // Get the name of the selected image
            // Upload image to server if required, or use imageUri to include in request
        }

        // send request to update the book record to the REST API
        CarService carService = ApiUtils.getCarService();
        Call<Car> call = carService.updateCar(user.getToken(), category, seats,  price, mileage, manufacturer,  model,
                year, imageName, "available");
        // execute
        call.enqueue(new Callback<Car>() {
            @Override
            public void onResponse(Call<Car> call, Response<Car> response) {

                // for debug purpose
                Log.d("MyApp:", "Update Request Response: " + response.raw().toString());

                if (response.code() == 200) {
                    // server return success code for update request
                    // get updated book object from response
                    Car updatedCar = response.body();

                    // display message
                    displayUpdateSuccess(updatedCar.getManufacturer() + " updated successfully.");


                }
                else if (response.code() == 401) {
                    // unauthorized error. invalid token, ask user to relogin
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
                else {
                    // server return other error
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("MyApp: ", response.toString());
                }

            }

            @Override
            public void onFailure(Call<Car> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
            }
        });
    }
    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayUpdateSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // end this activity and forward user to BookListActivity
                        Intent intent = new Intent(getApplicationContext(), CarListActivity.class);
                        startActivity(intent);
                        finish();

                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
}