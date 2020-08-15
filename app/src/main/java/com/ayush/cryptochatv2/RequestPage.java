package com.ayush.cryptochatv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ayush.cryptochatv2.adapters.AdapterRequest;
import com.ayush.cryptochatv2.pojo.Users;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestPage extends AppCompatActivity {

    private AdapterRequest adapterRequest;
    private DatabaseReference refRequest;
    private FirebaseDatabase database;
    private FirebaseUser currentUser;
    private ImageButton close;
    private List<Users> requestList;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout loading;
    private TextView warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_page);
        initialize();
        getRequest();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onBackPressed(); }
        });
    }

    private void initialize() {
        close = findViewById(R.id.close);
        recyclerView = findViewById(R.id.requestRecyclerView);
        loading = findViewById(R.id.loading);
        warning = findViewById(R.id.warning);
        requestList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        refRequest = database.getReference("REQUESTS");
    }

    private void getRequest() {
        if(currentUser == null) return;
        final String CURRENT_UID = currentUser.getUid();
        refRequest.child(CURRENT_UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestList.clear();
                if(snapshot.getChildrenCount() == 1) {
                    adapterRequest = new AdapterRequest(RequestPage.this, requestList, CURRENT_UID, refRequest);
                    recyclerView.setAdapter(adapterRequest);
                    loading.setVisibility(View.GONE);
                    warning.setVisibility(View.VISIBLE);
                    return;
                }
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String userKey = ds.getKey();
                    if (userKey != null && !userKey.equals("empty")) {
                        database.getReference("USERS").child(userKey)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users users = snapshot.getValue(Users.class);
                                        requestList.add(users);
                                        adapterRequest = new AdapterRequest(RequestPage.this, requestList, CURRENT_UID, refRequest);
                                        recyclerView.setAdapter(adapterRequest);
                                        loading.setVisibility(View.GONE);
                                        warning.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_enter_animation, R.anim.activity_exit_animation);
        finish();
    }
}
