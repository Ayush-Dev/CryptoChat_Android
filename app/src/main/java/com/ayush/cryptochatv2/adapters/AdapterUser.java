package com.ayush.cryptochatv2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ayush.cryptochatv2.MessagePage;
import com.ayush.cryptochatv2.R;
import com.ayush.cryptochatv2.UserInfo;
import com.ayush.cryptochatv2.pojo.Users;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    private Context context;
    private List<Users> usersList;
    private String CURRENT_UID;

    public AdapterUser(Context context, List<Users> usersList, String currentUserId) {
        this.context = context;
        this.usersList = usersList;
        this.CURRENT_UID = currentUserId;
    }

    @NonNull
    @Override
    public AdapterUser.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_user, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterUser.MyHolder holder, int position) {
        final String profileName = usersList.get(position).getName();
        final String profileId = usersList.get(position).getUid();
        final String profileEmail = usersList.get(position).getEmail();
        holder.tvProfileName.setText(profileName);
        holder.tvProfileEmail.setText(profileEmail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce_animation));
                AnimationUtils.loadAnimation(context, R.anim.bounce_animation);
                Intent intent = new Intent(context, MessagePage.class);
                intent.putExtra("currentUserId", CURRENT_UID);
                intent.putExtra("userEmail", profileEmail);
                intent.putExtra("userId", profileId);
                intent.putExtra("userName", profileName);
                context.startActivity(intent);
            }
        });

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserInfo.class);
                intent.putExtra("currentUserId", CURRENT_UID);
                intent.putExtra("userEmail", profileEmail);
                intent.putExtra("userId", profileId);
                intent.putExtra("userName", profileName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        RoundedImageView ivProfileImage;
        TextView tvProfileName, tvProfileEmail;

        private MyHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvProfileName = itemView.findViewById(R.id.tvProfileName);
            tvProfileEmail = itemView.findViewById(R.id.tvProfileEmail);
        }
    }
}
