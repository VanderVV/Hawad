package com.example.myapplication;

import android.content.Intent;
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
    private static final String EMBEDDED_PASSWORD = "123456789";

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = staffNumEditText.getText().toString().trim();
                String password = staffPinEditText.getText().toString().trim();

                // First check embedded credentials
                if (username.equals(EMBEDDED_USERNAME) && password.equals(EMBEDDED_PASSWORD)) {
                    // Embedded login successful
                    startHomeActivity();
                    Toast.makeText(MainActivity.this, "Embedded login successful", Toast.LENGTH_SHORT).show();
                } else {
                    // If not embedded credentials, try API login
                    attemptApiLogin(username, password);
                }
            }
        });
    }

    private void attemptApiLogin(final String username, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_API_URL,
                response -> {
                    if (response.trim().equals("success")) {
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