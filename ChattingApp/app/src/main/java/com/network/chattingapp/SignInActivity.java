package com.network.chattingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

/*
NOTE: change the rule in Google Firebase Console: Storage -> rules
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
       allow read, write;
    }
  }
}
 */

public class SignInActivity extends AppCompatActivity {
    private ImageView imageViewAvatar;
    private TextInputEditText textInputSigninEmail, textInputSigninPassword, textInputSigninUsername;
    private Button buttonRegister;
    boolean imageSelected = false;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage; //For save user's image
    StorageReference storageReference;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Register User");
        setContentView(R.layout.activity_sign_in);

        imageViewAvatar = findViewById(R.id.imageViewSignIn);
        textInputSigninEmail = findViewById(R.id.textInputSignInEmail);
        textInputSigninPassword = findViewById(R.id.textInputSignInPassword);
        textInputSigninUsername = findViewById(R.id.textInputSignInUsername);
        buttonRegister = findViewById(R.id.buttonRegister);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageProfileChooser();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textInputSigninEmail.getText().toString();
                String password = textInputSigninPassword.getText().toString();
                String username = textInputSigninUsername.getText().toString();
                if(!email.equals("") && !password.equals("") && !username.equals("")){
                    registerUser(email, password, username);
                }
                else {
                    Toast.makeText(SignInActivity.this, "PLease complete all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void registerUser(String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    reference.child("Users").child(auth.getUid()).child("userName").setValue(username);
                    if(imageSelected){
                        //Se genera un ID aleatorio para almacenar imagen en la database
                        UUID randomID = UUID.randomUUID();
                        String imageName = "images/"+randomID+".jpg";
                        storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //Traemos la Url de la imagen seleccionada
                                StorageReference myStorageReference = firebaseStorage.getReference(imageName);
                                myStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Guardamos la imagen en la base de datos
                                        String filePath = uri.toString();
                                        reference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignInActivity.this, "Data saved in Database", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignInActivity.this, "Error saving data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                    else {
                        reference.child("Users").child(auth.getUid()).child("image").setValue(null);
                    }
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(SignInActivity.this, "Error registering user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void imageProfileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewAvatar);
            imageSelected = true;
        }
        else {
            imageSelected = false;
        }
    }
}