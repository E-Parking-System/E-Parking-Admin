package com.km.eparkingadmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userName, userEmail, userPassword, organisationName, parkingFees;
    private Button loginButton, registerButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;
    private LoadingAlert loader;
    private String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setIds();
        setListeners();
    }

    private void setIds() {
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        organisationName = findViewById(R.id.organisation_name);
        parkingFees = findViewById(R.id.parking_fees);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        loader = new LoadingAlert();
    }

    private void setListeners() {
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    private boolean validateFields() {
        if (userName.getText().toString().trim().isEmpty()) {
            userName.requestFocus();
            userName.setError("Enter user name");
            return false;
        }
        if (!isEmailValid(userEmail.getText().toString())) {
            userEmail.requestFocus();
            userEmail.setError("Enter valid email");
            return false;
        }
        if (userPassword.getText().toString().trim().isEmpty()) {
            userPassword.requestFocus();
            userPassword.setError("Enter password");
            return false;
        }
        if (organisationName.getText().toString().trim().isEmpty()) {
            organisationName.requestFocus();
            organisationName.setError("Enter organisation name");
            return false;
        }
        if (parkingFees.getText().toString().trim().isEmpty()) {
            parkingFees.requestFocus();
            parkingFees.setError("Enter parking fees");
            return false;
        }
        return true;
    }

    private void performRegistration() {
        String userDetailsString = userName.getText().toString() + "~/" + userEmail.getText().toString() + "~/" + organisationName.getText().toString() + "~/" + parkingFees.getText().toString();
        AppPreferences appPreferences = new AppPreferences(this);
        appPreferences.setUserDetails(userDetailsString);

        loader.show(getSupportFragmentManager(), "loader");
        firebaseAuth.createUserWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loader.dismiss();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            addUsersDetails();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    private void addUsersDetails() {
        String key = firebaseAuth.getCurrentUser().getUid();
        UserModel user = new UserModel();
        user.setUserEmail(firebaseAuth.getCurrentUser().getEmail());
        user.setUserName(userName.getText().toString());
        user.setOrganisationName(organisationName.getText().toString());
        user.setParkingFee(Double.valueOf(parkingFees.getText().toString()));

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/admin/" + key, user);
        firebaseDatabase.updateChildren(childUpdates);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.register_button:
                if (validateFields()) {
                    performRegistration();
                }
                break;
        }
    }

    private boolean isEmailValid(String email) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2}+[a-z]*");
        return emailPattern.matcher(email).matches() && !email.trim().isEmpty();
    }
}
