package com.ozalp.chatapp;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.ozalp.chatapp.databinding.ChatRowBinding;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    List<Messages> messagesList = new ArrayList<>();
    String whoAmI;
    public ChatAdapter(List<Messages> messagesList, String whoAmI){
        this.messagesList = messagesList;
        this.whoAmI = whoAmI;
    }

    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ChatRowBinding rowBinding = ChatRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ChatHolder(rowBinding);
    }

    public void onBindViewHolder(ChatHolder holder, int position){

        if (messagesList.get(position).sender.matches(whoAmI)){
            holder.chatRowBinding.message.setText(messagesList.get(position).message);
            holder.chatRowBinding.message.setGravity(Gravity.RIGHT);
        }
        else {
            holder.chatRowBinding.message.setText(messagesList.get(position).message);
        }

    }

    public int getItemCount(){
        return messagesList.size();
    }
    public class ChatHolder extends RecyclerView.ViewHolder {
        ChatRowBinding chatRowBinding;
        public ChatHolder(ChatRowBinding chatRowBinding){
            super(chatRowBinding.getRoot());
            this.chatRowBinding = chatRowBinding;
        }
    }
}
