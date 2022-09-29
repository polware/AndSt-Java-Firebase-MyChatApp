package com.network.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverPasswordActivity extends AppCompatActivity {
    private TextInputEditText textInputRecoverPassword;
    private Button buttonResetPassword;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Reset Password");
        setContentView(R.layout.activity_recover_password);

        textInputRecoverPassword = findViewById(R.id.textInputRecoverPassword);
        buttonResetPassword = findViewById(R.id.buttonRecoverPassword);
        auth = FirebaseAuth.getInstance();

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textInputRecoverPassword.getText().toString();
                if(!email.equals("")){
                    passwordReset(email);
                }
            }
        });

    }

    public void passwordReset(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RecoverPasswordActivity.this, "PLease check your email", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RecoverPasswordActivity.this, "Error: email not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}