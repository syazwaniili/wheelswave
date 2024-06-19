package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.model.FailLogin;
import com.example.lab_rest.model.User;
import com.example.lab_rest.remote.ApiUtils;
import com.example.lab_rest.remote.UserService;
import com.example.lab_rest.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    // add comment
    private EditText edtUsername;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get references to form elements
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
    }

    /**
     * Login button action handler
     */
    public void loginClicked(View view) {

        // get username and password entered by user
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        // validate form, make sure it is not empty
        if (validateLogin(username, password)) {
            // if not empty, login using REST API
            doLogin(username, password);
        }

    }

    /**
     * Call REST API to login
     *
     * @param username username
     * @param password password
     */
    private void doLogin(String username, String password) {

        // implement this
        // get UserService instance
        UserService userService = ApiUtils.getUserService();

        // prepare the REST API call using the service interface
        //Call<User> call = userService.login(username, password);
        Call<User> call;
        if (username.contains("@")) {
            call = userService.loginEmail(username, password);
        } else {
            call = userService.login(username, password);
        }

        // execute the REST API call
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()) {  // code 200
                    // parse response to POJO
                    User user = (User) response.body();
                    if (user != null && user.getToken() != null) {
                        // successful login. server replies a token value
                        displayToast("Login successful");
                        displayToast("Token: " + user.getToken());

                        // store value in Shared Preferences
                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);
                        // forward user to MainActivity
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                    else {
                        // server return success but no user info replied
                        displayToast("Login error");
                    }
                }
                else {  // other than 200
                    // try to parse the response to FailLogin POJO
                    String errorResp = null;
                    try {
                        errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        displayToast(e.getError().getMessage());
                    } catch (Exception e) {
                        Log.e("MyApp:", e.toString()); // print error details to error log
                        displayToast("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
                Log.e("MyApp:", t.toString()); // print error details to error log
            }
        });
    }

    /**
     * Validate value of username and password entered. Client side validation.
     * @param username
     * @param password
     * @return
     */
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Display a Toast message
     * @param message message to be displayed inside toast
     */
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}