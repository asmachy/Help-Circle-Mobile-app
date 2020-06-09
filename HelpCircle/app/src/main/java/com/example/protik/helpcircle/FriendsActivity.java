package com.example.protik.helpcircle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView friend_list_view;
    private List<Friend> friend_list;
    private List<String> friend_id;

    FriendRecyclerAdapter friendRecyclerAdapter;

    private DatabaseReference mFriendDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        friend_id = new ArrayList<>();
        friend_list = new ArrayList<>();
        friend_list_view = findViewById(R.id.friend_list_view);

        friendRecyclerAdapter = new FriendRecyclerAdapter(friend_list);
        friend_list_view.setLayoutManager(new LinearLayoutManager(this));
        friend_list_view.setAdapter(friendRecyclerAdapter);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUser.getUid());
        mFriendDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                Toast.makeText(getApplicationContext(), "key: " + dataSnapshot.getKey(), Toast.LENGTH_LONG).show();

                //String key = dataSnapshot.getKey();
                Friend friend = dataSnapshot.getValue(Friend.class);

                friend_id.add(dataSnapshot.getKey());
                friend_list.add(friend);

                friendRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                int friendIndex = friend_id.indexOf(dataSnapshot.getKey());

                if (friendIndex > -1) {

                    friend_id.remove(friendIndex);
                    friend_list.remove(friendIndex);

                    //friendRecyclerAdapter.notifyDataSetChanged();
                    friendRecyclerAdapter.notifyItemRemoved(friendIndex);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
