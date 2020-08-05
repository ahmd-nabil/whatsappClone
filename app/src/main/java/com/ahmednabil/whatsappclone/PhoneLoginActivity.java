package com.ahmednabil.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private EditText mPhoneNumber;
    private EditText mVerificationCode;
    private Button sendCodeButton, verifyCodeButton;
    private DatabaseReference rootRef;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String authCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        initializeViews();
        rootRef = FirebaseDatabase.getInstance().getReference();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mPhoneNumber.setVisibility(View.INVISIBLE);
                sendCodeButton.setVisibility(View.INVISIBLE);
                mVerificationCode.setVisibility(View.VISIBLE);
                verifyCodeButton.setVisibility(View.VISIBLE);
                authCode = s;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mPhoneNumber.setVisibility(View.VISIBLE);
                sendCodeButton.setVisibility(View.VISIBLE);
                mVerificationCode.setVisibility(View.INVISIBLE);
                verifyCodeButton.setVisibility(View.INVISIBLE);
                Toast.makeText(PhoneLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void initializeViews() {
        this.mPhoneNumber = findViewById(R.id.phone_number_input);
        this.mVerificationCode = findViewById(R.id.verification_code);
        this.sendCodeButton = findViewById(R.id.sendVerificationCode);
        this.verifyCodeButton = findViewById(R.id.verify_code);
    }

    public void sendVerificationCode(View view) {
        String phoneNumber = mPhoneNumber.getText().toString();
        if(TextUtils.isEmpty(phoneNumber))
            Toast.makeText(this, "Phone Number can't be empty", Toast.LENGTH_SHORT).show();
        else {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
            }
    }

    public void verifyCode(View view) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(authCode, mVerificationCode.getText().toString());
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if(currentUser != null) {
                        rootRef.child("users").child(currentUser.getUid()).setValue("");
                        Toast.makeText(PhoneLoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(PhoneLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void sendUserToMainActivity(){
        Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}