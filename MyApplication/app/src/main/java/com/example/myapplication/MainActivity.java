package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editStaffNum;
    EditText editPinNum;
    Button btnLogin;

    String tempStuffNum = "";
    int tempStuffPin = 0;
    boolean isStaffNum = false;
    boolean isStaffPin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editStaffNum = findViewById(R.id.StaffNum);
        editPinNum = findViewById(R.id.StaffPin);
        btnLogin = findViewById(R.id.BtnLogin);

//        Intent inc = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(inc);
//        finish();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sanitize
                if (!(editStaffNum.getText().toString().length() > 0) || !(editPinNum.getText().toString().length() > 0)) {return;}

                tempStuffNum = editStaffNum.getText().toString();
                tempStuffPin = Integer.parseInt(editPinNum.getText().toString());


                if (tempStuffNum.equalsIgnoreCase("admin") || tempStuffNum.equals("202211") || tempStuffNum.equalsIgnoreCase("VANDER")) {
                    isStaffNum = true;
                    //is valid number
                    Toast.makeText(getApplicationContext(), "Correct content " + tempStuffNum, Toast.LENGTH_SHORT).show();
                } else {
                    isStaffNum = !isStaffNum;
                    Toast.makeText(getApplicationContext(), "Invalid Input, Enter number", Toast.LENGTH_SHORT).show();
                    Intent inc = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(inc);
                }
                //Pin is 2022
                if ((tempStuffPin > 0) && (tempStuffPin == 2022)) {
                    isStaffPin = true;
                    //is valid number
                    Toast.makeText(getApplicationContext(), "Correct PIN " + tempStuffNum, Toast.LENGTH_SHORT).show();
                } else {
                    isStaffPin = !isStaffPin;
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    Intent inc = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(inc);
                }


                if (isStaffPin && isStaffNum) {
                    Intent inc = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(inc);


                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Details Retry", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}