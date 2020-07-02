package com.ahmednabil.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText register_email;
    private EditText register_password;
    private Button register_button;
    private TextView already_have_account_link, register_with_phone_number_link;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        initializeViews();
        already_have_account_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            sendUserToLoginActivity();
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

    }


    private void createNewAccount() {
        String email = register_email.getText().toString();
        String password = register_password.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email can't be empty.", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password can't be empty.", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String currentUID = firebaseAuth.getCurrentUser().getUid();
                        rootRef.child("users").child(currentUID).setValue("");
                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }

    private void initializeViews() {
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_button = findViewById(R.id.register_button);
        already_have_account_link = findViewById(R.id.already_have_account_link);
        register_with_phone_number_link = findViewById(R.id.register_with_phone_number_link);
    }


    private void sendUserToLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void sendUserToMainActivity(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}