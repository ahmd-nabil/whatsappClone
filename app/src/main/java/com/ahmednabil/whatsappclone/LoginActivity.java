package com.ahmednabil.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar loginProgressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;

    private EditText login_email;
    private EditText login_password;
    private TextView forget_password_link;
    private Button login_button;
    private TextView create_new_account_link, login_using_phone_number_link;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        initializeViews();


        create_new_account_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserWithEmailAndPassword();
            }
        });
    }


    private void loginUserWithEmailAndPassword() {
        String email = login_email.getText().toString();
        String password = login_password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email can't be empty.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password can't be empty.", Toast.LENGTH_SHORT).show();
        } else {
            loginProgressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendUserToMainActivity();
                    }else {
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            loginProgressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void initializeViews() {
        loginProgressBar = findViewById(R.id.login_progress_bar);
        loginProgressBar.setVisibility(View.INVISIBLE);
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        forget_password_link= findViewById(R.id.forget_password_link);
        login_button = findViewById(R.id.login_button);
        create_new_account_link = findViewById(R.id.create_new_account_link);
        login_using_phone_number_link = findViewById(R.id.login_using_phone_number_link);
    }

    // this is double check.
    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null){
            sendUserToMainActivity();
        }
    }

    private void sendUserToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);    }

    private void sendUserToMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}