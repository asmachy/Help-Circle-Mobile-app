package com.example.protik.helpcircle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage, editBtn;
    private TextView nameTxt, phoneTxt, emailTxt;
    private Button sendButton, cancelBtn;

    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mFirestore;
    private String mCurrentState;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFriendReqReceivedDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        profileImage = findViewById(R.id.imageView);
        editBtn = findViewById(R.id.editText);
        nameTxt = findViewById(R.id.my_name);
        phoneTxt = findViewById(R.id.mobile_no);
        emailTxt = findViewById(R.id.email_no);
        sendButton = findViewById(R.id.addBtnId);
        cancelBtn = findViewById(R.id.declineBtnId);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendReqReceivedDatabase = FirebaseDatabase.getInstance().getReference().child("Received_Friend_Req");
        mCurrentState = "not_friend";
        cancelBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setEnabled(false);


        final String user_id = getIntent().getStringExtra("user_id");
        retrieveData(user_id);

        mFriendRequestDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)) {

                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                    if (req_type.equals("received")) {

                        mCurrentState = "req_received";
                        sendButton.setText("Accept Request");

                        cancelBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setEnabled(true);

                    } else if (req_type.equals("sent")){

                        mCurrentState = "req_sent";
                        sendButton.setText("Cancel Request");

                        cancelBtn.setVisibility(View.INVISIBLE);
                        cancelBtn.setEnabled(false);
                    }
                } else {

                    mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(user_id)) {

                                mCurrentState = "friends";
                                sendButton.setText("UnFriend");

                                cancelBtn.setVisibility(View.INVISIBLE);
                                cancelBtn.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFriends(user_id);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriends();
            }
        });
    }

    private void deleteFriends() {
        Toast.makeText(getApplicationContext(), "decline", Toast.LENGTH_SHORT).show();
    }

    private void createFriends(final String uId) {

        sendButton.setEnabled(false);

        if (mCurrentState.equals("not_friend")) {

            mFriendRequestDatabase.child(mCurrentUser.getUid()).child(uId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        mFriendRequestDatabase.child(uId).child(mCurrentUser.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //
                                String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                Map<String, String> receivedReqMap = new HashMap<>();
                                receivedReqMap.put("date", currentDate);
                                receivedReqMap.put("user_id", mCurrentUser.getUid());
                                mFriendReqReceivedDatabase.child(uId).child(mCurrentUser.getUid()).setValue(receivedReqMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getApplicationContext(), "Successfully sent request", Toast.LENGTH_SHORT).show();
                                        sendButton.setEnabled(true);
                                        mCurrentState = "req_sent";
                                        sendButton.setText("Cancel Request");

                                        cancelBtn.setVisibility(View.INVISIBLE);
                                        cancelBtn.setEnabled(false);
                                    }
                                });

                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Failed Sending Request", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (mCurrentState.equals("req_sent")) {

            mFriendRequestDatabase.child(mCurrentUser.getUid()).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mFriendRequestDatabase.child(uId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqReceivedDatabase.child(uId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sendButton.setEnabled(true);
                                    mCurrentState = "not_friend";
                                    sendButton.setText("Add Friend");

                                    cancelBtn.setVisibility(View.INVISIBLE);
                                    cancelBtn.setEnabled(false);
                                }
                            });

                        }
                    });
                }
            });
        }

        if (mCurrentState.equals("req_received")) {

            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            Map<String, String> friendMap = new HashMap<>();
            friendMap.put("date", currentDate);
            friendMap.put("user_id", uId);

            mFriendDatabase.child(mCurrentUser.getUid()).child(uId).setValue(friendMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                Map<String, String> anotherFriendMap = new HashMap<>();
                anotherFriendMap.put("date", currentDate);
                anotherFriendMap.put("user_id", mCurrentUser.getUid());

                mFriendDatabase.child(uId).child(mCurrentUser.getUid()).setValue(anotherFriendMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFriendRequestDatabase.child(mCurrentUser.getUid()).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mFriendRequestDatabase.child(uId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mFriendReqReceivedDatabase.child(mCurrentUser.getUid()).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                sendButton.setEnabled(true);
                                                mCurrentState = "friends";
                                                sendButton.setText("UnFriend");

                                                cancelBtn.setVisibility(View.INVISIBLE);
                                                cancelBtn.setEnabled(false);
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });
                }
            });
        }

    }

    private void retrieveData(String uId) {

        mFirestore.collection("Users").document(uId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String name = task.getResult().getString("name");
                    String phone = task.getResult().getString("phone");
                    String email = task.getResult().getString("email");

                    nameTxt.setText(name);
                    phoneTxt.setText(phone);
                    emailTxt.setText(email);

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, "ERROR: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
