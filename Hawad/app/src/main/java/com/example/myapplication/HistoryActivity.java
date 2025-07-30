package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private final List<OperationHistoryItem> historyItems = new ArrayList<>();
    private RequestQueue requestQueue;
    private EditText searchEditText;
    private TextView dateRangeText;
    private MaterialButton dateRangeButton;
    private String selectedOperation = null;
    private String dateFrom = null;
    private String dateTo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize views
        recyclerView = findViewById(R.id.historyRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        dateRangeText = findViewById(R.id.dateRangeText);
        dateRangeButton = findViewById(R.id.dateRangeButton);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(historyItems);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        // Setup search and filter
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedOperation = s.toString().trim().toLowerCase();
                fetchHistory();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dateRangeButton.setOnClickListener(v -> showDateRangeDialog());

        fetchHistory();
    }

    private void showDateRangeDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Select Date Range");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_date_range, null);
        builder.setView(dialogView);

        MaterialButton btnFrom = dialogView.findViewById(R.id.btnFromDate);
        MaterialButton btnTo = dialogView.findViewById(R.id.btnToDate);
        TextView tvFrom = dialogView.findViewById(R.id.tvFromDate);
        TextView tvTo = dialogView.findViewById(R.id.tvToDate);

        if (dateFrom != null) tvFrom.setText(dateFrom);
        if (dateTo != null) tvTo.setText(dateTo);

        btnFrom.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Start Date")
                    .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                dateFrom = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date(selection));
                tvFrom.setText(dateFrom);
            });
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_FROM");
        });

        btnTo.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select End Date")
                    .build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                dateTo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date(selection));
                tvTo.setText(dateTo);
            });
            datePicker.show(getSupportFragmentManager(), "DATE_PICKER_TO");
        });

        builder.setPositiveButton("Apply", (dialog, which) -> {
            dateRangeText.setText((dateFrom != null ? dateFrom : "Any") + " to " +
                    (dateTo != null ? dateTo : "Any"));
            fetchHistory();
        });

        builder.setNegativeButton("Clear", (dialog, which) -> {
            dateFrom = null;
            dateTo = null;
            dateRangeText.setText("Any date");
            fetchHistory();
        });

        builder.show();
    }

    private void fetchHistory() {
        String url = buildHistoryUrl();
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                this::parseHistoryResponse,
                error -> Toast.makeText(HistoryActivity.this, "Error loading history", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private String buildHistoryUrl() {
        Uri.Builder builder = Uri.parse("https://xpanxn.co.za/api.php").buildUpon();
        builder.appendQueryParameter("get_history", "true");
        builder.appendQueryParameter("limit", "100");

        if (selectedOperation != null && !selectedOperation.isEmpty()) {
            builder.appendQueryParameter("operation", selectedOperation);
        }
        if (dateFrom != null) {
            builder.appendQueryParameter("date_from", dateFrom);
        }
        if (dateTo != null) {
            builder.appendQueryParameter("date_to", dateTo);
        }

        return builder.build().toString();
    }

    private void parseHistoryResponse(JSONArray response) {
        try {
            historyItems.clear();
            for (int i = 0; i < response.length(); i++) {
                JSONObject item = response.getJSONObject(i);
                historyItems.add(new OperationHistoryItem(
                        item.getString("operation"),
                        item.getInt("old_status"),
                        item.getInt("new_status"),
                        item.getString("timestamp")
                ));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}