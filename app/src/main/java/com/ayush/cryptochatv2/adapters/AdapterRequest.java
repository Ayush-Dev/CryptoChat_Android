package com.ayush.cryptochatv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.cryptochatv2.R;
import com.ayush.cryptochatv2.pojo.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterRequest extends RecyclerView.Adapter<AdapterRequest.MyHolder>{

    private Context context;
    private DatabaseReference refRequest, refUser, refContact;
    private List<Users> requestList;
    private String currentUid, uid;

    public AdapterRequest(Context context, List<Users> requestList, String currentUid, DatabaseReference refRequest) {
        this.context = context;
        this.requestList = requestList;
        this.currentUid = currentUid;
        this.refRequest = refRequest;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_request, parent, false);
        initialize();
        return new AdapterRequest.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String profileName = requestList.get(position).getName();
        final String profileEmail = requestList.get(position).getEmail();
        holder.reqFullName.setText(profileName);
        holder.reqEmail.setText(profileEmail);

        holder.acceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptRequest(profileEmail);
            }
        });

        holder.rejectRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectRequest(profileEmail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    private void initialize() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        refUser = database.getReference("USERS");
        refContact = database.getReference("CONTACTS");
    }

    private void acceptRequest(String email) {
        refUser.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            Users users;
                            for(DataSnapshot ds: snapshot.getChildren()) {
                                users = ds.getValue(Users.class);
                                if(users == null) return;
                                uid = users.getUid();
                            }
                            refContact.child(currentUid).child(uid).setValue("confirm");
                            refContact.child(uid).child(currentUid).setValue("confirm");
                            refRequest.child(uid).child(currentUid).removeValue();
                            refRequest.child(currentUid).child(uid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(context,
                                                        "Request Accepted",
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void rejectRequest(String email) {
        refUser.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            Users users;
                            for(DataSnapshot ds: snapshot.getChildren()) {
                                users = ds.getValue(Users.class);
                                if(users == null) return;
                                uid = users.getUid();
                            }
                            refRequest.child(currentUid).child(uid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(context, "Request Rejected",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        Button acceptRequest, rejectRequest;
        TextView reqFullName, reqEmail;

        private MyHolder(@NonNull View itemView) {
            super(itemView);
            reqFullName = itemView.findViewById(R.id.reqFullName);
            reqEmail = itemView.findViewById(R.id.reqEmail);
            acceptRequest = itemView.findViewById(R.id.acceptRequest);
            rejectRequest = itemView.findViewById(R.id.rejectRequest);
        }
    }
}
