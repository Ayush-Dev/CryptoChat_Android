package com.ayush.cryptochatv2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ayush.cryptochatv2.LoginPage;
import com.ayush.cryptochatv2.R;
import com.ayush.cryptochatv2.pojo.Messages;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.MyHolder>{

    private Context context;
    private List<Messages> messageList;
    private String currentUid;

    public AdapterMessage(Context context, List<Messages> messageList, String currentUid) {
        this.context = context;
        this.messageList = messageList;
        this.currentUid = currentUid;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_design, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Messages message = messageList.get(position);
        String owner = message.getOwner();
        if(owner.equals(currentUid)) {
            holder.receiveMessage.setVisibility(View.INVISIBLE);
            holder.sendMessage.setVisibility(View.VISIBLE);
            holder.sendMessage.setText(message.getMessage());
        }
        else {
            holder.receiveMessage.setVisibility(View.VISIBLE);
            holder.sendMessage.setVisibility(View.INVISIBLE);
            holder.receiveMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        TextView receiveMessage, sendMessage;

        private MyHolder(@NonNull View itemView) {
            super(itemView);
            receiveMessage = itemView.findViewById(R.id.receiveMessage);
            sendMessage = itemView.findViewById(R.id.sendMessage);
        }
    }
}
