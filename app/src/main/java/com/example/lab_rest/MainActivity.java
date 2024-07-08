package com.example.lab_rest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab_rest.model.User;
import com.example.lab_rest.sharedpref.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    private TextView tvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvHello = findViewById(R.id.tvHello);

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        if (!spm.isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            User user = spm.getUser();
            if (user.getUsername() != null) {
                tvHello.setText("Hello " + user.getUsername());
            } else {
                tvHello.setText("Hello!");
            }
        }
    }

    public void logoutClicked(View view) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void carListClicked(View view) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        Intent intent;

        if ("admin".equals(user.getRole()) || "superadmin".equals(user.getRole())) {
            intent = new Intent(getApplicationContext(), CarListActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), CustomerCarListActivity.class);
        }

        startActivity(intent);
    }
}
