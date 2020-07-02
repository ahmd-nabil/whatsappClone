package com.ahmednabil.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView settingsProfileImage;
    private EditText settingsUserName;
    private EditText settingsAboutMe;
    private Button updateSettingsButton;
    private static final String ABOUT_ME = "Hey there, I am using whatsApp.";

    DatabaseReference rootRef;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rootRef = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        initializeViews();
        updateSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserSettings();
            }
        });
    }


    private void initializeViews() {
        settingsProfileImage = findViewById(R.id.set_profile_image);
        settingsUserName = findViewById(R.id.set_user_name);
        settingsAboutMe = findViewById(R.id.set_about_me);
        updateSettingsButton = findViewById(R.id.update_settings_button);
    }

    private void updateUserSettings() {
        String username = settingsUserName.getText().toString();
        String aboutMe = settingsAboutMe.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Username can't be empty.", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            if(TextUtils.isEmpty(aboutMe)) aboutMe = ABOUT_ME;
            profileMap.put("username", username);
            profileMap.put("aboutMe", aboutMe);
            rootRef.child("users").child(currentUser.getUid()).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        sendUserToMainActivity();
                    } else {
                        Toast.makeText(SettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }
}