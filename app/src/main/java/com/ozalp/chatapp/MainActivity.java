package com.ozalp.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ozalp.chatapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityMainBinding binding;
    FirebaseFirestore firestore;
    TextView email;
    TextView password;
    LinearLayout linearLayoutUserLogin;
    LinearLayout linearLayoutMessages;
    String whoAmI = null; //email
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        System.out.println("app is started");
        init();

        if (auth != null){
            linearLayoutUserLogin.setVisibility(View.GONE);
            linearLayoutMessages.setVisibility(View.VISIBLE);
        }

    }

    private void init(){
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        email = binding.email;
        password = binding.password;
        linearLayoutUserLogin = binding.linearLayoutUserLogin;
        linearLayoutMessages = binding.linearLayoutMessages;
    }

    public void login(View view){
        auth.signInWithEmailAndPassword(
                email.getText().toString(),
                password.getText().toString()
        ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                linearLayoutUserLogin.setVisibility(View.GONE);
                linearLayoutMessages.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logout(View view){
        auth.signOut();
        linearLayoutUserLogin.setVisibility(View.VISIBLE);
        linearLayoutMessages.setVisibility(View.GONE);

    }

}