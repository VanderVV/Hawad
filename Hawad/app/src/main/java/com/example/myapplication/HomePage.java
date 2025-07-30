package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

public class HomePage extends AppCompatActivity {

    // UI Components
    private CircularProgressIndicator tempProgress, waterProgress;
    private TextView tempValue, waterValue;
    private SwitchMaterial pumpSwitch, wateringSwitch, systemSwitch;
    private RequestQueue requestQueue;
    private MaterialCardView reportCard;
    private static final String API_URL = "https://xpanxn.co.za/api.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);
        // Bind UI components
        tempProgress = findViewById(R.id.progress_temp_value);
        waterProgress = findViewById(R.id.progress_water_value);
        tempValue = findViewById(R.id.temp_value_text);
        waterValue = findViewById(R.id.water_value_text);
        pumpSwitch = findViewById(R.id.switchpump);
        wateringSwitch = findViewById(R.id.switchwatering);
        systemSwitch = findViewById(R.id.switchSytsem);
        reportCard = findViewById(R.id.reportCard);

        reportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        // Set up periodic data refresh
        refreshData();

        // Set up switch listeners


        pumpSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {

                if(!systemSwitch.isChecked())
                {
                    return;
                }
                updateSwitchStatus("pump", isChecked);
            }
        });
        wateringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if(!systemSwitch.isChecked())
                {
                    return;
                }
                updateSwitchStatus("watering", isChecked);
            }
        });
        systemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                updateSwitchStatus("system", isChecked);
            }
        });


    }

    private void refreshData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            // Update temperature
                            int temp = response.getInt("temperature");
                            tempProgress.setProgress(temp);
                            tempValue.setText(temp + "°C");

                            // Update water level
                            int waterLevel = response.getInt("water_level");
                            waterProgress.setProgress(waterLevel);
                            waterValue.setText(waterLevel + "%");

                            // Update switch states
                            pumpSwitch.setChecked(response.getInt("pump_status") == 1);
                            wateringSwitch.setChecked(response.getInt("watering_status") == 1);
                            systemSwitch.setChecked(response.getInt("system_status") == 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomePage.this, "Error fetching data from Server", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

        // Refresh every 5 seconds
        new android.os.Handler().postDelayed(this::refreshData, 5000);
    }

    private void updateSwitchStatus(String switchType, boolean isChecked) {
        JSONObject postData = new JSONObject();
        try {
            postData.put(switchType + "_status", isChecked ? 1 : 0);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    API_URL,
                    postData,
                    response -> {},
                    error -> {}
            );

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}