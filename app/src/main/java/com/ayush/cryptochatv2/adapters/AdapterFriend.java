package com.ayush.cryptochatv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ayush.cryptochatv2.R;
import com.ayush.cryptochatv2.pojo.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterFriend extends RecyclerView.Adapter<AdapterFriend.MyHolder>{

    private Context context;
    private DatabaseReference refChats, refContacts;
    private List<Users> friendList;
    private String currentUid;

    public AdapterFriend(Context context, List<Users> friendList, String currentUid, DatabaseReference refChats, DatabaseReference refContacts) {
        this.context = context;
        this.friendList = friendList;
        this.currentUid = currentUid;
        this.refChats = refChats;
        this.refContacts = refContacts;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_friend, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        System.out.println();
        final String uid = friendList.get(position).getUid();
        holder.tvProfileName.setText(friendList.get(position).getName());
        holder.ibRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend(uid, currentUid);
            }
        });
    }

    @Override
    public int getItemCount() {return friendList.size();}

    private void removeFriend(String uid, String currentUid) {
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

    static class MyHolder extends RecyclerView.ViewHolder {

        ImageButton ibRemoveFriend;
        TextView tvProfileName;

        private MyHolder(@NonNull View itemView) {
            super(itemView);
            ibRemoveFriend = itemView.findViewById(R.id.ibRemoveFriend);
            tvProfileName = itemView.findViewById(R.id.tvProfileName);
        }
    }
}
