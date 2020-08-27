package com.ayush.cryptochatv2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ayush.cryptochatv2.adapters.AdapterUser;
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


public class ChatFragment extends Fragment {

    private AdapterUser adapterUser;
    private FirebaseDatabase database;
    private List<Users> contactList;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout loading;
    private String CURRENT_UID;
    private TextView warning;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        initialize();
        getContacts();
//        contactList.add(new Users("Ayush", "email", "uid", ""));
//        adapterUser = new AdapterUser(getActivity(), contactList, CURRENT_UID);
//        recyclerView.setAdapter(adapterUser);

        return view;
    }

    private void initialize() {
        recyclerView = view.findViewById(R.id.chatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loading = view.findViewById(R.id.loading);
        warning = view.findViewById(R.id.warning);
        contactList = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
    }

    private void getContacts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;
        CURRENT_UID = user.getUid();
        DatabaseReference refContacts = database.getReference("CONTACTS").child(user.getUid());
        refContacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                if(snapshot.getChildrenCount() == 1) {
                    adapterUser = new AdapterUser(getActivity(), contactList, CURRENT_UID);
                    recyclerView.setAdapter(adapterUser);
                    loading.setVisibility(View.GONE);
                    warning.setVisibility(View.VISIBLE);
                    return;
                }
                for(DataSnapshot ds: snapshot.getChildren()) {
                    String uid = ds.getKey();
                    if(uid != null && !uid.equals("empty")) {
                        database.getReference("USERS").child(uid)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users users = snapshot.getValue(Users.class);
                                        contactList.add(users);
                                        adapterUser = new AdapterUser(getActivity(), contactList, CURRENT_UID);
                                        recyclerView.setAdapter(adapterUser);
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
    public void onDestroy() {
        super.onDestroy();
        loading.stopShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        loading.stopShimmer();
    }

    @Override
    public void onResume() {
        super.onResume();
        loading.startShimmer();
    }

}
