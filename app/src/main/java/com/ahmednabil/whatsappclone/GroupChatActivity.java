package com.ahmednabil.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {
    private DatabaseReference rootRef;
    private DatabaseReference usersRef;
    private DatabaseReference currentGroupRef;
    private DatabaseReference groupMessagesRef;

    private Toolbar mToolBar;
    private ScrollView mScrollView;
    private TextView messageDisplay;
    private ImageButton sendButton;
    private EditText messageEditText;
    private String groupId;
    private String groupName;
    private String currentUID, currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");

        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        currentGroupRef = rootRef.child("groups").child(groupId);
        groupMessagesRef = currentGroupRef.child("messages");

        initializeViews();
        getUserInfo();





        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0) {
                    sendButton.setEnabled(false);
                    sendButton.setClickable(false);
                }
                else {
                    sendButton.setEnabled(true);
                    sendButton.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        sendButton.setClickable(false);
        sendButton.setEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupMessagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()) new DisplayMessage(messageDisplay).execute(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()) new DisplayMessage(messageDisplay).execute(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void initializeViews() {
        mToolBar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle(groupName);
        mScrollView = findViewById(R.id.my_scroll_view);
        messageDisplay = findViewById(R.id.group_chat_text_view);
        sendButton = findViewById(R.id.sendButton); //sendButton.setEnabled(false); // enabled to false by default (until there's message).
        messageEditText = findViewById(R.id.newMessage);
    }

    private void getUserInfo() {
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(!TextUtils.isEmpty(currentUID)){
            usersRef.child(currentUID).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot!=null && snapshot.getValue()!= null) currentUsername = snapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void sendMessage() {
        Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show();
        String msg = messageEditText.getText().toString(); // don't have to check if empty >> i made button clickable false until there's text in messageEditText.

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");   // to get the date format
        String currentDate = currentDateFormat.format(calendar.getTime());

        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm:ss a");    // to get time format
        String currentTime = currentTimeFormat.format(calendar.getTime());

        DatabaseReference newMessageRef = groupMessagesRef.push();

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", currentUID);
        messageData.put("date", currentDate);
        messageData.put("time", currentTime);
        messageData.put("message", msg);

        newMessageRef.updateChildren(messageData);
        messageEditText.setText(null);
    }

    public class DisplayMessage extends AsyncTask<DataSnapshot, Void, String> {
        private String messageDate;
        private String messageTime;
        private String message;
        private String senderId;
        private String senderName;
        private WeakReference<TextView> mMessageDisplay;

        public DisplayMessage(TextView messageDisplay) {
            mMessageDisplay = new WeakReference<>(messageDisplay);
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(DataSnapshot... snapshots) {
            messageDate = snapshots[0].child("date").getValue().toString();
            messageTime = snapshots[0].child("time").getValue().toString();
            message = snapshots[0].child("message").getValue().toString();
            senderId = snapshots[0].child("sender").getValue().toString();
            usersRef.child(senderId).child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderName = snapshot.getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
            while(senderName == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return senderName + " :\n" + message + "\n" + messageDate + "\t" + messageTime + "\n\n\n";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMessageDisplay.get().append(s);
        }
    }
}