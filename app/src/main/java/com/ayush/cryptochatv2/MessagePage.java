package com.ayush.cryptochatv2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ayush.cryptochatv2.adapters.AdapterMessage;
import com.ayush.cryptochatv2.pojo.Messages;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class MessagePage extends AppCompatActivity {

    private AdapterMessage adapterMessage;
    private Animation sendAnimation;
    private DatabaseReference refChats;
    private EditText etMessageText;
    private ImageButton ibMessageSend, ibMessageBack, ibInfo;
    private List<Messages> messageList;
    private RecyclerView recyclerView;
    private RoundedImageView profilePicture;
    private String CURRENT_UID, USER_ID, USER_EMAIL, USER_NAME;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);
        initialize();
        loadMessages();
//        messageList.add(new Messages("hola", "amigo"));
        recyclerView.setAdapter(adapterMessage);

        ibMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibMessageSend.startAnimation(sendAnimation);
                String text = etMessageText.getText().toString().trim();
                if(!text.isEmpty()) {
                    sendMessage(text);
                }
            }
        });

        ibMessageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagePage.super.onBackPressed();
            }
        });

        ibInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MessagePage.this,
                        new Pair<View, String>(profilePicture, "profilePicture"));
                Intent intent = new Intent(MessagePage.this, UserInfo.class);
                intent.putExtra("currentUserId", CURRENT_UID);
                intent.putExtra("userName", USER_NAME);
                intent.putExtra("userEmail", USER_EMAIL);
                intent.putExtra("userId", USER_ID);
                startActivity(intent, options.toBundle());
            }
        });

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

                if(adapterMessage.getItemCount() > 0 && i4 < i7) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(adapterMessage.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
    }

    private void initialize() {
        TextView tvMessageName = findViewById(R.id.tvMessageName);
        ibMessageSend = findViewById(R.id.ibMessageSend);
        ibMessageBack = findViewById(R.id.ibMessageBack);
        profilePicture = findViewById(R.id.ivMessageProfileImage);
        ibInfo = findViewById(R.id.ibInfo);
        etMessageText = findViewById(R.id.etMessageText);
        recyclerView = findViewById(R.id.messages_recyclerView);
        sendAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce_animation);
        messageList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        CURRENT_UID = getIntent().getStringExtra("currentUserId");
        USER_ID = getIntent().getStringExtra("userId");
        USER_EMAIL = getIntent().getStringExtra("userEmail");
        USER_NAME = getIntent().getStringExtra("userName");
        tvMessageName.setText(USER_NAME);
        refChats = FirebaseDatabase.getInstance().getReference("CHATS");
        adapterMessage = new AdapterMessage(MessagePage.this, messageList, CURRENT_UID);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else System.out.println("NULL");
    }

    private void loadMessages() {
        refChats.child(CURRENT_UID).child(USER_ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages message;
                message = snapshot.getValue(Messages.class);
                messageList.add(message);
                adapterMessage.notifyDataSetChanged();
                recyclerView.setAdapter(adapterMessage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

    private void sendMessage(String text) {
        Messages message = new Messages(CURRENT_UID, text.trim());
        refChats.child(CURRENT_UID).child(USER_ID).child(message.getTimestamp()).setValue(message);
        refChats.child(USER_ID).child(CURRENT_UID).child(message.getTimestamp()).setValue(message);
        etMessageText.setText(null);
    }

}
