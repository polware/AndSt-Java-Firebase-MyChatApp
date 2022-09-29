package com.network.chattingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.network.chattingapp.ChatActivity;
import com.network.chattingapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private List<String> usersList;
    private String userName;
    private Context context;
    FirebaseDatabase database;
    DatabaseReference reference;

    public UsersAdapter(List<String> usersList, String userName, Context context) {
        this.usersList = usersList;
        this.userName = userName;
        this.context = context;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView textViewUsersName;
        private ImageView imageViewUsersPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            textViewUsersName = itemView.findViewById(R.id.textViewUsersName);
            imageViewUsersPicture = itemView.findViewById(R.id.imageViewUsersPicture);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        reference.child("Users").child(usersList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String otherUser = snapshot.child("userName").getValue().toString();
                String imageURL = snapshot.child("image").getValue().toString();
                holder.textViewUsersName.setText(otherUser);
                if(imageURL.equals("null")){
                    holder.imageViewUsersPicture.setImageResource(R.drawable.usersignin);
                }
                else {
                    Picasso.get().load(imageURL).into(holder.imageViewUsersPicture);
                }

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("userName", userName);
                        intent.putExtra("otherUser", otherUser);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

}
