package com.km.eparkingadmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView userName, organisationName, parkingFees;
    private Button scanQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initIds();
        setListeners();
        setData();
    }

    private void initIds() {
        userName = findViewById(R.id.user_name);
        organisationName = findViewById(R.id.organisation_name);
        parkingFees = findViewById(R.id.parking_fees);
        scanQR = findViewById(R.id.scan_qr_code);
    }

    private void setData() {
        String[] userDetails = new AppPreferences(this).getUserDetails().split("~/");

        String name = userDetails[0];
        String orgName = userDetails[2];
        String fees = userDetails[3];

        userName.setText(name);
        organisationName.setText(orgName);
        parkingFees.setText(fees);

    }

    private void setListeners() {
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CheckEntryActivity.class));
            }
        });
    }

}
