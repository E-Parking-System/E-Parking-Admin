package com.km.eparkingadmin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CheckEntryActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private TextView name, vehicleName, licenceNumber, checkInTime, checoutTime, parkedTime, totalAmount;
    private Button cencelButton, confirmButton;
    private Group paymentGroup, qrScannerGroup, commonGroup;
    private ZXingScannerView qrView;
    private String TAG = "CheckEntryActivity";
    private String vehicleLicenceNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_entry);

        setIds();
        setListeners();
        setQrView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrView.stopCamera();
    }

    private void setIds() {
        qrView = findViewById(R.id.scanner_camera);
        name = findViewById(R.id.customer_name);
        vehicleName = findViewById(R.id.vehicle_name);
        licenceNumber = findViewById(R.id.licence_number);
        checkInTime = findViewById(R.id.check_in_time);
        checoutTime = findViewById(R.id.checkout_time);
        parkedTime = findViewById(R.id.parked_time);
        totalAmount = findViewById(R.id.amount_to_pay);
        cencelButton = findViewById(R.id.cancel_button);
        confirmButton = findViewById(R.id.confirm_button);
        paymentGroup = findViewById(R.id.payment_group);
        commonGroup = findViewById(R.id.common_group);
        qrScannerGroup = findViewById(R.id.scanner_group);

    }

    private void setQrView() {
        List<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        qrView.setResultHandler(this);
        qrView.setLaserEnabled(false);
        qrView.setFormats(formats);
        qrView.startCamera();
        qrView.setBorderColor(Color.parseColor("#C1BFC6"));
    }

    private void setCommonData(String[] customerDetails) {
        name.setText(customerDetails[0]);
        vehicleName.setText(customerDetails[1]);
        licenceNumber.setText(customerDetails[2]);
        commonGroup.setVisibility(View.VISIBLE);
    }

    private void setPaymentData(String licenceNumber) {
        AppPreferences appPreferences = new AppPreferences(this);
        PaymentModel paymentModel = appPreferences.getVehicleInfo(licenceNumber);
        if (paymentModel == null) {
            appPreferences.addNewParkingVehicle(licenceNumber);
            checkInTime.setText(timestampToTime(System.currentTimeMillis()));
        } else {
            checkInTime.setText(timestampToTime(paymentModel.getCheckInTime()));
            checoutTime.setText(timestampToTime(System.currentTimeMillis()));

            String time = convertSecondsToTimeString((int) ((System.currentTimeMillis() - paymentModel.getCheckInTime()) / 1000L));
            parkedTime.setText(time);
            totalAmount.setText(String.valueOf(getTotalBill(Integer.parseInt(time.split(":")[0]))));
            paymentGroup.setVisibility(View.VISIBLE);

        }


    }

    private void setListeners() {
        cencelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppPreferences(CheckEntryActivity.this).removeVehicelFromList(vehicleLicenceNumber);
                finish();
            }
        });
    }

    @Override
    public void handleResult(Result result) {
        Log.d(TAG, "QR code : " + result.getText());
        String[] customerDetails = result.getText().split("~/");
        if (customerDetails.length != 3) {
            Toast.makeText(CheckEntryActivity.this, "Invalid QR Code...!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        qrView.stopCamera();
        qrScannerGroup.setVisibility(View.GONE);
        vehicleLicenceNumber = customerDetails[2];
        setCommonData(customerDetails);
        setPaymentData(vehicleLicenceNumber);


    }

    private double getTotalBill(int hours) {
        double parkingFees = Double.parseDouble(new AppPreferences(this).getUserDetails().split("~/")[3]);
        return parkingFees * hours;
    }

    private String timestampToTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
        Date netDate = new Date(timestamp);
        return sdf.format(netDate);
    }


    String convertSecondsToTimeString(int inputSeconds) {
        int hours = (inputSeconds / 60) / 60;
        int minutes = inputSeconds / 60;
        return hours + ":" + minutes;
    }
}
