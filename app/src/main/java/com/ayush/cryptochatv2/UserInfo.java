package com.ayush.cryptochatv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInfo extends AppCompatActivity {

    private DatabaseReference refChats;
    private String USER_EMAIL, USER_NAME, CURRENT_UID, USER_ID;
    private TextView email, messageCount;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initialize();
        setUI();
        getMessageCount();
    }

    private void initialize() {
        email = findViewById(R.id.userInfoEmail);
        messageCount = findViewById(R.id.userInfoMessageCount);
        toolbar = findViewById(R.id.toolbar);
        CURRENT_UID = getIntent().getStringExtra("currentUserId");
        USER_NAME = getIntent().getStringExtra("userName");
        USER_EMAIL = getIntent().getStringExtra("userEmail");
        USER_ID = getIntent().getStringExtra("userId");
        refChats = FirebaseDatabase.getInstance().getReference("CHATS");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressLint("SetTextI18n")
    private void setUI() {
        toolbar.setTitle(USER_NAME);
        email.setText(USER_EMAIL);
    }

    private void getMessageCount() {
        refChats.child(CURRENT_UID).child(USER_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long size = snapshot.getChildrenCount();
                messageCount.setText(Long.toString(size));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
