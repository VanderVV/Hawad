package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

    int progr;
    ProgressBar progress;
    TextView _text_view_progress, _waterlevel, _textphlevel, _texttemperature;
    MaterialCardView controlsCArd, reportCard, statsCard;

    Button btnreport, _btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        progress = findViewById(R.id.progress_bar);
        _text_view_progress = findViewById(R.id.text_view_progress);
        _waterlevel = findViewById(R.id.waterlevel);
        _textphlevel = findViewById(R.id.phlevel);
        _texttemperature = findViewById(R.id.temperaturelevel);
        controlsCArd = findViewById(R.id.controlsCard);
        reportCard = findViewById(R.id.reportCard);
        statsCard = findViewById(R.id.statsCard);
//        _btnRefresh statsCard = findViewById(R.id.btnRefresh);

//        Walie Added
        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomePage.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        controlsCArd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityControls.class);
                startActivity(intent);
            }
        });
        reportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);

            }
        });

        statsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityReport.class);
                startActivity(intent);
            }
        });


        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 2000);
               // Toast.makeText(getApplicationContext(), "refreshed", Toast.LENGTH_SHORT).show();
                //updateProgressBar();
                BedroomStatus();
            }
        };
        r.run();

    }

    @SuppressLint("SetTextI18n")
    private void updateProgressBar() {
        progress.setProgress(100);
        _text_view_progress.setText(Integer.toString(100));
    }
    @Override
    protected void onResume() {
        super.onResume();
        BedroomStatus();
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
                int progress_values = Integer.parseInt(s[4]);
                int percentage_progress = progress_values *(100/24);
                if (progress_values >= 24)
                    percentage_progress = 100;

                String perc_str = percentage_progress + "%";
                String WaterLevel_progress = s[4] + "L";

                _textphlevel.setText(s[3]);
                _texttemperature.setText(s[2] + "°C");
                _waterlevel.setText(WaterLevel_progress);
                progress.setProgress(percentage_progress);
                _text_view_progress.setText(perc_str);
            }catch (Exception e)
            {

            }

        }
    };
}