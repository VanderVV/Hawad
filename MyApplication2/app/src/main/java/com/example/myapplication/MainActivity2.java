package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    RecyclerView mRecyclerView;
    List<EmailData> mEmailData = new ArrayList<>();
    EmailData mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity2.this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity2.this,
                DividerItemDecoration.VERTICAL));

        mRecyclerView.setLayoutManager(mLinearLayoutManager);



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                field[0] = "report";
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
            //Toast.makeText(MainActivity2.this, Temp_data.temp_data, Toast.LENGTH_SHORT).show();

            try {

                // echo$row["name"]."#".$row["action"]."#".$row["time"]."@";
                String[] s = Temp_data.temp_data.split("@");
                String[] k;
                for (String value : s) {
                    k = value.split("#");
                    mEmail = new EmailData(k[0], k[1],
                            Temp_data.temp_data.replace("#","").replace("@",""), k[2]);
                    mEmailData.add(mEmail);
                }


                MailAdapter mMailAdapter = new MailAdapter(MainActivity2.this, mEmailData);
                mRecyclerView.setAdapter(mMailAdapter);
            }
            catch (Exception ignore)
            {

            }
        }

    };
}
