package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
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
import androidx.appcompat.widget.SwitchCompat;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Objects;

public class ActivityControls extends AppCompatActivity {

    TextView _waterlevel, _textphlevel, _texttemperature;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch pumpin, pumpOut, turnOff;
    Button _btnBack, _btnRefresh, _btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
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


//        Walie Added
        _btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent);
            }
        });

        _btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
//        Walie Added
        _btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityControls.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        pumpin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    UpdateVAlues("WaterIn", "A");
                else
                    UpdateVAlues("WaterIn", "0");
            }
        });


        pumpOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    UpdateVAlues("WaterOut", "B");
                else
                    UpdateVAlues("WaterOut", "0");
            }
        });


        turnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    UpdateVAlues("SystemOff", "C");
                else
                    UpdateVAlues("SystemOff", "0");
            }
        });
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 2500);
//                Toast.makeText(getApplicationContext(), "refreshed", Toast.LENGTH_SHORT).show();
                BedroomStatus();
            }
        };
        r.run();
    }

    void UpdateVAlues(String keyword, String Value) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String[] field = new String[2];
                field[0] = "MyValues";
                field[1] = "Status";
                //Creating array for data
                String[] data = new String[2];
                data[0] = keyword;
                data[1] = Value;
                try {
                    PutData putData = new PutData("http://edufied.tech/water_wander.php", "POST", field, data);
                    if (putData.startPut()) {

                        if (putData.onComplete()) {
                            Temp_data.temp_data = putData.getResult();
                        }
                    }
                } catch (Exception exception) {
                    //SignUpText.setText("result");
                }
                handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();



    }

    void BedroomStatus() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String[] field = new String[2];
                field[0] = "dashboardvalues";
                field[1] = "Status";
                //Creating array for data
                String[] data = new String[2];
                data[0] = "SelectBedroom";
                data[1] = "homeData";
                try {
                    PutData putData = new PutData("http://edufied.tech/water_wander.php", "POST", field, data);
                    if (putData.startPut()) {

                        if (putData.onComplete()) {
                            Temp_data.temp_data = putData.getResult();
                        }
                    }
                } catch (Exception exception) {
                    //SignUpText.setText("result");
                }
                handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            try {
                String[] s = Temp_data.temp_data.split("#");
                String WaterLevel_progress = s[4] + "L";

                _textphlevel.setText(s[3]);
                _texttemperature.setText(s[2] + "°C");
                _waterlevel.setText(WaterLevel_progress);
                pumpin.setChecked(Temp_data.temp_data.contains("A"));
                pumpOut.setChecked(Temp_data.temp_data.contains("B"));
                turnOff.setChecked(Temp_data.temp_data.contains("C"));

            } catch (Exception e) {

            }
        }


    };

    @Override
    protected void onResume() {
        super.onResume();
        BedroomStatus();
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