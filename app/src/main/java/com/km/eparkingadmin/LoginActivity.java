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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userEmail, userPassword;
    private Button loginButton, registerButton;
    private FirebaseAuth firebaseAuth;
    private String TAG = "LoginActivity";

    private LoadingAlert loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setIds();
        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setIds() {
        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        firebaseAuth = FirebaseAuth.getInstance();
        loader = new LoadingAlert();
    }

    private void setListeners() {
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    private boolean validateFields() {
        if (!isEmailValid(userEmail.getText().toString())) {
            userEmail.setError("Enter email");
            return false;
        }
        if (userPassword.getText().toString().trim().isEmpty()) {
            userPassword.setError("Enter password");
            return false;
        }
        return true;
    }

    private void performUserLogin() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        loader.show(getSupportFragmentManager(), "loader");
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            getUserInfo();
                        } else {
                            loader.dismiss();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("admin").child(firebaseAuth.getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loader.dismiss();
                UserModel userData = dataSnapshot.getValue(UserModel.class);
                if (userData != null) {
                    String userDetails = userData.getUserName() + "~/" + userData.getUserEmail() + "~/" + userData.getOrganisationName() + "~/" + userData.getParkingFee();
                    new AppPreferences(LoginActivity.this).setUserDetails(userDetails);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                } else {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(LoginActivity.this, "User info not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if (validateFields()) {
                    performUserLogin();
                }
                break;
            case R.id.register_button:
                startActivity(new Intent(this, RegistrationActivity.class));
                finish();
                break;

        }
    }

    private boolean isEmailValid(String email) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]{2}+[a-z]*");
        return emailPattern.matcher(email).matches() && !email.trim().isEmpty();
    }
}
