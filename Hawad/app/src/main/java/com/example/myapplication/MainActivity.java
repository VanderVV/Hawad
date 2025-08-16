package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText staffNumEditText, staffPinEditText;
    private Button loginButton;
    private RequestQueue requestQueue;
    private static final String LOGIN_API_URL = "https://xpanxn.co.za/login.php";
    private static final String EMBEDDED_USERNAME = "admin";
    private static final String EMBEDDED_PASSWORD = "12345";
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        staffNumEditText = findViewById(R.id.StaffNum);
        staffPinEditText = findViewById(R.id.StaffPin);
        loginButton = findViewById(R.id.BtnLogin);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Check for saved credentials
        checkSavedCredentials();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = staffNumEditText.getText().toString().trim();
                String password = staffPinEditText.getText().toString().trim();

                // First check embedded credentials
                if (username.equals(EMBEDDED_USERNAME) && password.equals(EMBEDDED_PASSWORD)) {
                    // Embedded login successful
                    saveCredentials(username, password);
                    startHomeActivity();
                    Toast.makeText(MainActivity.this, "Embedded login successful", Toast.LENGTH_SHORT).show();
                } else {
                    // If not embedded credentials, try API login
                    attemptApiLogin(username, password);
                }
            }
        });
    }

    private void checkSavedCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
        String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");

        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            // Auto-fill the fields
            staffNumEditText.setText(savedUsername);
            staffPinEditText.setText(savedPassword);

            // Optionally attempt auto-login here if desired
            // attemptApiLogin(savedUsername, savedPassword);
        }
    }

    private void saveCredentials(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    private void attemptApiLogin(final String username, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_API_URL,
                response -> {
                    if (response.trim().equals("success")) {
                        saveCredentials(username, password);
                        startHomeActivity();
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(MainActivity.this, "Login error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void startHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomePage.class);
        startActivity(intent);
        finish();
    }
}