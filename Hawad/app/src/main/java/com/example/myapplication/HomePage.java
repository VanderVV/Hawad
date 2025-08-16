package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
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
    private int currentWaterLevel = 0;
    private boolean hasShownHighWaterWarning = false;
    private static final int MAX_WATER_LEVEL = 100;

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
                if (!systemSwitch.isChecked()) {
                    showSystemOffDialog();
                    pumpSwitch.setChecked(false);
                    return;
                }

                if (isChecked && currentWaterLevel >= MAX_WATER_LEVEL) {
                    showTankFullDialog();
                    pumpSwitch.setChecked(false);

                    return;
                }

                updateSwitchStatus("pump", isChecked);
            }
        });

        wateringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (!systemSwitch.isChecked()) {
                    showSystemOffDialog();
                    wateringSwitch.setChecked(false);
                    return;
                }

                if (isChecked && currentWaterLevel <= 10) {
                    showLowWaterDialog();
                    wateringSwitch.setChecked(false);
                    return;
                }

                updateSwitchStatus("watering", isChecked);
            }
        });

        systemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    updateSwitchStatus("watering", false);
                    updateSwitchStatus("pump", false);
                    updateSwitchStatus("system", false);
                    wateringSwitch.setChecked(false);
                    pumpSwitch.setChecked(false);
                } else {
                    updateSwitchStatus("system", true);
                }
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

                            // Update water level (converted to percentage with max 26)
                            int rawWaterLevel = response.getInt("water_level");
                            int tankPercentage = (rawWaterLevel * 100) / 21;
                            tankPercentage = (tankPercentage >=95) ? 100 : tankPercentage;
                            currentWaterLevel =tankPercentage;
                            waterProgress.setProgress(currentWaterLevel);
                            waterValue.setText(currentWaterLevel + "%");
                            if(rawWaterLevel >= 20)
                            {
                                updateSwitchStatus("pump", false);
                                pumpSwitch.setChecked(false);
                            }
                            // Check for high water level (only show warning once until level drops)
                            if (currentWaterLevel >= MAX_WATER_LEVEL * 0.9 && !hasShownHighWaterWarning) {
                                showHighWaterLevelWarning();
                                hasShownHighWaterWarning = true;


                            } else if (currentWaterLevel < MAX_WATER_LEVEL * 0.9) {
                                hasShownHighWaterWarning = false;
                            }

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

    private void showSystemOffDialog() {
        new AlertDialog.Builder(this)
                .setTitle("System Off")
                .setMessage("Please turn ON the system first to control pumps")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showHighWaterLevelWarning() {
        new AlertDialog.Builder(this)
                .setTitle("High Water Level")
                .setMessage("Water level is " + currentWaterLevel + "%. Consider turning off the pump.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showLowWaterDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Low Water Level")
                .setMessage("Water level is too low (" + currentWaterLevel + "%) for watering. Please refill the tank.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showTankFullDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tank Full")
                .setMessage("Water level is " + currentWaterLevel + "%. Tank is full, cannot turn on pump.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pumpSwitch.setChecked(false);
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}