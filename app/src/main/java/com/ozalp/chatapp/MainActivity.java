package com.ozalp.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    TextView writeMessage;
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
            setWhoAreYou();
        }

    }

    public void setWhoAreYou(){
        firestore.collection("Messages").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots != null){
                    for (int i = 0; i<queryDocumentSnapshots.getDocuments().size(); i++){
                        if (!(queryDocumentSnapshots.getDocuments().get(i).getId().matches(whoAmI))){
                            whoAreYou = queryDocumentSnapshots.getDocuments().get(i).getId();
                            System.out.println("who are u? " + whoAreYou);
                            binding.messageUsername.setText(whoAreYou);
                        }
                    }
                }
            }
        });
    }

    public void send(View view){
        String message = writeMessage.getText().toString();
        if(!(message.isEmpty())){

            Map messageMap = new HashMap();
            messageMap.put("message",message);
            messageMap.put("sender",whoAmI);
            messageMap.put("time", FieldValue.serverTimestamp());

            firestore.collection("Messages/"+whoAmI+"/message")
                    .document()
                    .set(messageMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    firestore.collection("Messages/"+whoAreYou+"/message").document().set(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            writeMessage.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                }
            });



        }
    }

    private void getMessages() {
        try {
            firestore.collection("Messages"+"/"+whoAmI+"/message")
                    .orderBy("time", Query.Direction.ASCENDING)
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
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                layoutManager.setStackFromEnd(true); // ilk açılışta sondan başlıyor
                                binding.messageView.setLayoutManager(layoutManager);
                                ChatAdapter chatAdapter = new ChatAdapter(messagesList, whoAmI);
                                binding.messageView.setAdapter(chatAdapter);
                                //chatAdapter.notifyDataSetChanged();
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
        writeMessage = binding.writeMessage;
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
                setWhoAreYou();
                whoAmI = email.getText().toString();
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