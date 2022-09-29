package com.network.chattingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.network.chattingapp.R;
import com.network.chattingapp.models.InboxModel;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    private List<InboxModel> list;
    private String userName;
    private boolean status;
    private int send, receive;

    public MessagesAdapter(List<InboxModel> list, String userName) {
        this.list = list;
        this.userName = userName;

        status = false;
        send = 1;
        receive = 2;
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            if(status){
                textView = itemView.findViewById(R.id.textViewSend);
            }
            else {
                textView = itemView.findViewById(R.id.textViewReceive);
            }
        }
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == send){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_receive, parent, false);
        }
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getFrom().equals(userName)){
            status = true;
            return send;
        }
        else {
            status = false;
            return receive;
        }
    }
}
