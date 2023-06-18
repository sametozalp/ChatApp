package com.ozalp.chatapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozalp.chatapp.databinding.ChatRowBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    List messageList = new ArrayList<>();
    public ChatAdapter(List messageList){
        this.messageList = messageList;
    }

    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType){
        ChatRowBinding rowBinding = ChatRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ChatHolder(rowBinding);
    }

    public void onBindViewHolder(ChatHolder holder, int position){
        holder.chatRowBinding.message.setText(messageList.get(position).toString());
    }

    public int getItemCount(){
        return messageList.size();
    }
    public class ChatHolder extends RecyclerView.ViewHolder {
        ChatRowBinding chatRowBinding;
        public ChatHolder(ChatRowBinding chatRowBinding){
            super(chatRowBinding.getRoot());
            this.chatRowBinding = chatRowBinding;
        }
    }
}
