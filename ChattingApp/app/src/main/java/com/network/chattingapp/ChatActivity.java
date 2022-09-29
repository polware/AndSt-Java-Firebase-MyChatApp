package com.network.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.network.chattingapp.adapters.MessagesAdapter;
import com.network.chattingapp.models.InboxModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChat;
    private ImageView  imageViewBack;
    private TextView textViewChat;
    private EditText editTextMessage;
    private FloatingActionButton fabSend;
    private String userName, otherUser;
    private MessagesAdapter messagesAdapter;
    private List<InboxModel> list;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser fUser;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageViewBack = findViewById(R.id.imageViewBack);
        textViewChat = findViewById(R.id.textViewChat);
        editTextMessage = findViewById(R.id.editTextMultiLineMessage);
        fabSend = findViewById(R.id.fabSendMessage);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);

        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        userName = getIntent().getStringExtra("userName");
        otherUser = getIntent().getStringExtra("otherUser");
        textViewChat.setText(otherUser);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message = editTextMessage.getText().toString();
                if(!message.equals("")){
                    sendMessage(message);
                    editTextMessage.setText("");
                }
            }
        });

        getMessage();
    }

    public void sendMessage(String message) {
        String key = reference.child("Messages").child(userName).child(otherUser).push().getKey();
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("from", userName);
        //Save message in the Database
        reference.child("Messages").child(userName).child(otherUser).child(key).setValue(messageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            reference.child("Messages").child(otherUser).child(userName).child(key).setValue(messageMap);
                        }
                    }
                });
    }

    public void getMessage(){
        reference.child("Messages").child(userName).child(otherUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                InboxModel inboxModel = snapshot.getValue(InboxModel.class);
                list.add(inboxModel);
                messagesAdapter.notifyDataSetChanged();
                recyclerViewChat.scrollToPosition(list.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messagesAdapter = new MessagesAdapter(list, userName);
        recyclerViewChat.setAdapter(messagesAdapter);
    }
}