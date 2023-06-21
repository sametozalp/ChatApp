package com.ozalp.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.ozalp.chatapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityMainBinding binding;
    FirebaseFirestore firestore;
    TextView email;
    TextView password;
    LinearLayout linearLayoutUserLogin;
    LinearLayout linearLayoutMessages;
    String whoAmI = null; // my email
    String whoAreYou = null; // your email
    TextView messageUsername;
    List<Messages> messagesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        System.out.println("app is started");
        init();


        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            //view and who am i ?
            whoAmI = user.getEmail();
            System.out.println("welcome: " + whoAmI);
            linearLayoutUserLogin.setVisibility(View.GONE);
            linearLayoutMessages.setVisibility(View.VISIBLE);

            getMessages();
        }
    }

    private void getMessages() {
        try {
            firestore.collection("Messages"+"/"+whoAmI+"/message")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value != null){
                                Map map = new HashMap();
                                for(int i = 0; i<value.size(); i++){
                                    map = value.getDocuments().get(i).getData();
                                    Messages message = new Messages((String) map.get("sender"), (String) map.get("message"));
                                    messagesList.add(message);
                                }
                                binding.messageView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                                ChatAdapter chatAdapter = new ChatAdapter(messagesList);
                                binding.messageView.setAdapter(chatAdapter);

                                chatAdapter.notifyDataSetChanged();
                            }

                            if(error != null){
                                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void init(){
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        email = binding.email;
        password = binding.password;
        linearLayoutUserLogin = binding.linearLayoutUserLogin;
        linearLayoutMessages = binding.linearLayoutMessages;
        messageUsername = binding.messageUsername;
        messagesList = new ArrayList<>();
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
                getMessages();
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