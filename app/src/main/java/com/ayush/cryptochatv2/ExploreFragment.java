package com.ayush.cryptochatv2;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.cryptochatv2.adapters.AdapterFriend;
import com.ayush.cryptochatv2.pojo.Users;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExploreFragment extends Fragment {

    private Animation animationSearch;
    private AdapterFriend adapterFriend;
    private DatabaseReference refUsers, refRequests, refChats, refContacts;
    private EditText exploreSearchBar;
    private FirebaseDatabase database;
    private ImageButton exploreSearch;
    private List<Users> friendList;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout loading;
    private String buttonColor, buttonType, currentUid, uid, userName, userEmail;
    private TextView warning, requests;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_explore, container, false);
        initialize();
        getFriends();

        exploreSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exploreSearch.startAnimation(animationSearch);
                String email = exploreSearchBar.getText().toString();
                if(email.isEmpty()) System.out.println("Enter Email !!");
                else getSearchDetails(email);
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), RequestPage.class),
                        ActivityOptions.makeCustomAnimation(view.getContext(),
                                R.anim.activity_enter_animation,
                                R.anim.activity_exit_animation).toBundle());
            }
        });
        return view;
    }

    private void initialize() {
        exploreSearchBar = view.findViewById(R.id.exploreSearchBar);
        exploreSearch = view.findViewById(R.id.exploreSearch);
        recyclerView = view.findViewById(R.id.exploreRecyclerView);
        loading = view.findViewById(R.id.loading);
        warning = view.findViewById(R.id.warning);
        requests = view.findViewById(R.id.requests);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendList = new ArrayList<>();
        animationSearch = AnimationUtils.loadAnimation(getContext(), R.anim.bounce_animation);
        database = FirebaseDatabase.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        refUsers = database.getReference("USERS");
        refChats = database.getReference("CHATS");
        refContacts = database.getReference("CONTACTS");
        refRequests = database.getReference("REQUESTS");
    }

    private void getFriends() {
        if(currentUid == null) return;
        refContacts.child(currentUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendList.clear();
                if(snapshot.getChildrenCount() == 1) {
                    adapterFriend = new AdapterFriend(getActivity(),
                            friendList,
                            currentUid,
                            refChats,
                            refContacts);
                    recyclerView.setAdapter(adapterFriend);
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
                                        friendList.add(users);
                                        adapterFriend = new AdapterFriend(getActivity(),
                                                friendList,
                                                currentUid,
                                                refChats,
                                                refContacts);
                                        recyclerView.setAdapter(adapterFriend);
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

    private void getSearchDetails(String email) {
        refUsers.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            Users users;
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                users = ds.getValue(Users.class);
                                if (users == null) return;
                                uid = users.getUid();
                                userName = users.getName();
                                userEmail = users.getEmail();
                            }
                            refRequests.child(uid).child(currentUid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()) {
                                                buttonType = "Cancel Request";
                                                buttonColor = "#222222";
                                                showBottomSheet(userName, userEmail, buttonType, buttonColor);
                                                return;
                                            }
                                            refContacts.child(currentUid).child(uid)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(snapshot.exists()) {
                                                                buttonType = "Remove Friend";
                                                                buttonColor = "#FF0033";
                                                                showBottomSheet(userName, userEmail, buttonType, buttonColor);
                                                                return;
                                                            }
                                                            buttonType = "Send Friend Request";
                                                            buttonColor = "#0F4C75";
                                                            showBottomSheet(userName, userEmail, buttonType, buttonColor);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                        else System.out.println("No user Found");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showBottomSheet(String fullName, String email, String buttonText, String buttonColor) {
        BottomSheetDialog bottomSheetDialog;
        final Button sheetButton;
        TextView sheetName, sheetEmail;
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet,
                (LinearLayout) view.findViewById(R.id.bottomSheetContainer));
        bottomSheetDialog = new BottomSheetDialog(Objects.requireNonNull(getContext()), R.style.BottomSheetDialogTheme);
        sheetButton = bottomSheetView.findViewById(R.id.sheetButton);
        sheetEmail = bottomSheetView.findViewById(R.id.sheetEmail);
        sheetName = bottomSheetView.findViewById(R.id.sheetName);

        sheetName.setText(fullName);
        sheetEmail.setText(email);
        sheetButton.setText(buttonText);
        sheetButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(buttonColor)));
        sheetButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String newButtonText = sheetButton.getText().toString();
                if (newButtonText.equals("Send Friend Request")) {
                    sendFriendRequest();
                    sheetButton.setBackgroundTintList(ColorStateList
                            .valueOf(Color.parseColor("#222222")));
                    sheetButton.setText("Cancel Request");

                } else if (newButtonText.equals("Cancel Request")) {
                    cancelFriendRequest();
                    sheetButton.setBackgroundTintList(ColorStateList
                            .valueOf(Color.parseColor("#0F4C75")));
                    sheetButton.setText("Send Friend Request");
                } else {
                    removeFriend();
                    sheetButton.setBackgroundTintList(ColorStateList
                            .valueOf(Color.parseColor("#0F4C75")));
                    sheetButton.setText("Send Friend Request");
                }
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void sendFriendRequest() {
        if (currentUid == null) return;
        refRequests.child(uid).child(currentUid).setValue("pending")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cancelFriendRequest() {
        if (currentUid == null) return;
        refRequests.child(uid).child(currentUid).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Toast.makeText(getContext(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeFriend() {
        if (currentUid == null) return;
        refContacts.child(uid).child(currentUid).removeValue();
        refContacts.child(currentUid).child(uid).removeValue();
        refChats.child(uid).child(currentUid).removeValue();
        refChats.child(currentUid).child(uid).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("Friend Removed");
                    }
                });
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
