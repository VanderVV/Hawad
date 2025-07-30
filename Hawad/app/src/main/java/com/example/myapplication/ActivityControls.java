package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityControls extends AppCompatActivity {

    TextView _waterlevel, _textphlevel, _texttemperature;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch pumpin, pumpOut, turnOff;
    Button _btnBack, _btnRefresh, _btnLogout;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        _waterlevel = findViewById(R.id.waterlevel);
        _textphlevel = findViewById(R.id.phlevel);
        _texttemperature = findViewById(R.id.temperaturelevel);
        pumpin = findViewById(R.id.switchpump);
        pumpOut = findViewById(R.id.switchwatering);
        turnOff = findViewById(R.id.switchSytsem);
        _btnBack = findViewById(R.id.btnBack);
        _btnLogout = findViewById(R.id.btnLogout);
        _btnRefresh = findViewById(R.id.btnRefresh);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        _btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), HomePage.class);
            startActivity(intent);
        });

        _btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}