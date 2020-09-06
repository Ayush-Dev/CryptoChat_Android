package com.ayush.cryptochatv2;

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
import com.ayush.cryptochatv2.security.KeyExchange;
import com.ayush.cryptochatv2.security.Secure;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessagePage extends AppCompatActivity {

    private AdapterMessage adapterMessage;
    private Animation sendAnimation;
    private DatabaseReference refChats;
    private EditText etMessageText;
    private ImageButton ibMessageSend, ibMessageBack, ibInfo;
    private List<Messages> messageList;
    private RecyclerView recyclerView;
    private RoundedImageView profilePicture;
    private String CURRENT_UID, USER_ID, USER_EMAIL, USER_NAME, USER_PUBLIC_KEY, SHARED_KEY;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_page);
        initialize();
        initializeSharedKey();
        loadMessages();
//        messageList.add(new Messages("hola", "amigo"));
//        recyclerView.setAdapter(adapterMessage);

        ibMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibMessageSend.startAnimation(sendAnimation);
                String text = etMessageText.getText().toString().trim();
                if(!text.isEmpty()) {
                    sendMessage(text.trim());
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
        CURRENT_UID = LoginPage.UID;
        USER_ID = getIntent().getStringExtra("userId");
        USER_EMAIL = getIntent().getStringExtra("userEmail");
        USER_NAME = getIntent().getStringExtra("userName");
        USER_PUBLIC_KEY = getIntent().getStringExtra("userKey");
        tvMessageName.setText(USER_NAME);
        refChats = FirebaseDatabase.getInstance().getReference("CHATS");
        adapterMessage = new AdapterMessage(MessagePage.this, messageList, LoginPage.UID);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeSharedKey() {
        ArrayList<Integer> publicKeyPackets = KeyExchange.generatePublicPackets(USER_PUBLIC_KEY);
        ArrayList<Integer> sharedKeyPackets = KeyExchange.generateSharedPackets(publicKeyPackets, LoginPage.PRIVATE_KEY_PACKETS);
        SHARED_KEY = KeyExchange.generateSharedKey(sharedKeyPackets);
    }

    private void loadMessages() {
        System.out.println("SHARED KEY " + LoginPage.EMAIL + ": " + SHARED_KEY);
        refChats.child(CURRENT_UID).child(USER_ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages message;
                message = snapshot.getValue(Messages.class);
                assert message != null;
                String text = message.getMessage();
                try {
                    text = Secure.decrypt(text, SHARED_KEY);
                    message.setMessage(text);
                } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException |
                        IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
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
        try {
            text = Secure.encrypt(text, SHARED_KEY);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException |
                NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        Messages message = new Messages(CURRENT_UID, text);
        refChats.child(CURRENT_UID).child(USER_ID).child(message.getTimestamp()).setValue(message);
        refChats.child(USER_ID).child(CURRENT_UID).child(message.getTimestamp()).setValue(message);
        etMessageText.setText(null);
    }

}
