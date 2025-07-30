package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class HomePage extends AppCompatActivity {
    private static final String TAG = "HomePage";
    private static final int REFRESH_DELAY_MS = 2000;
    private static final int MAX_WATER_LEVEL = 24;

    private ProgressBar progress;
    private TextView _text_view_progress, _waterlevel, _textphlevel, _texttemperature;
    private MaterialCardView controlsCArd, reportCard, statsCard;
    private Button _btnRefresh;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            try {
                if (Temp_data.temp_data == null || Temp_data.temp_data.isEmpty()) {
                    Log.w(TAG, "Empty or null data received");
                    return;
                }

                String[] s = Temp_data.temp_data.split("#");
                if (s.length < 5) {
                    Log.e(TAG, "Invalid data format received");
                    return;
                }

                int progressValue = Integer.parseInt(s[4]);
                int percentage = Math.min(progressValue * (100 / MAX_WATER_LEVEL), 100);

                _textphlevel.setText(s[3]);
                _texttemperature.setText(s[2] + "°C");
                _waterlevel.setText(progressValue + "L");
                progress.setProgress(percentage);
                _text_view_progress.setText(percentage + "%");
            } catch (Exception e) {
                Log.e(TAG, "Error processing data", e);
                Toast.makeText(HomePage.this, "Error updating data", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initializeViews();
        setupClickListeners();
        startAutoRefresh();
    }

    private void initializeViews() {
        progress = findViewById(R.id.progress_bar);
        _text_view_progress = findViewById(R.id.text_view_progress);
        _waterlevel = findViewById(R.id.waterlevel);
        _textphlevel = findViewById(R.id.phlevel);
        _texttemperature = findViewById(R.id.temperaturelevel);
        controlsCArd = findViewById(R.id.controlsCard);
        reportCard = findViewById(R.id.reportCard);
        statsCard = findViewById(R.id.statsCard);
        _btnRefresh = findViewById(R.id.btnRefresh);
    }

    private void setupClickListeners() {
        progress.setOnClickListener(v -> refreshActivity());

        controlsCArd.setOnClickListener(v -> {
            startActivity(new Intent(this, ActivityControls.class));
        });

        reportCard.setOnClickListener(v -> {
            // Intent for report activity when implemented
        });

        statsCard.setOnClickListener(v -> {
            startActivity(new Intent(this, ActivityReport.class));
        });

        if (_btnRefresh != null) {
            _btnRefresh.setOnClickListener(v -> BedroomStatus());
        }
    }

    private void refreshActivity() {
        startActivity(new Intent(this, HomePage.class));
        overridePendingTransition(0, 0);
        finish();
    }

    private void startAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                BedroomStatus();
                handler.postDelayed(this, REFRESH_DELAY_MS);
            }
        }, REFRESH_DELAY_MS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BedroomStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void BedroomStatus() {
        new Thread(() -> {
            String[] field = {"dashboardvalues", "Status"};
            String[] data = {"SelectBedroom", "homeData"};

            try {
                PutData putData = new PutData("http://edufied.tech/water_wander.php", "POST", field, data);
                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    if (result != null && !result.isEmpty()) {
                        Temp_data.temp_data = result;
                        handler.sendEmptyMessage(0);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Network error", e);
                handler.post(() ->
                        Toast.makeText(HomePage.this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}