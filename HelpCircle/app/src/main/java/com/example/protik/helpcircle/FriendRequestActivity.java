package com.example.protik.helpcircle;

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

import java.util.ArrayList;
import java.util.List;

public class FriendRequestActivity extends AppCompatActivity {

    private RecyclerView friend_request_list_view;
    private List<FriendRequest> friend_request_list;
    private List<String> friend_request_id;

    FriendRequestRecyclerAdapter friendRequestRecyclerAdapter;

    private DatabaseReference mFriendRequestReceivedDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        friend_request_id = new ArrayList<>();
        friend_request_list = new ArrayList<>();
        friend_request_list_view = findViewById(R.id.friend_request_list_view);

        friendRequestRecyclerAdapter = new FriendRequestRecyclerAdapter(friend_request_list);
        friend_request_list_view.setLayoutManager(new LinearLayoutManager(this));
        friend_request_list_view.setAdapter(friendRequestRecyclerAdapter);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendRequestReceivedDatabase = FirebaseDatabase.getInstance().getReference().child("Received_Friend_Req").child(mCurrentUser.getUid());
        mFriendRequestReceivedDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Toast.makeText(getApplicationContext(), "key: " + dataSnapshot.getKey(), Toast.LENGTH_LONG).show();

                //String key = dataSnapshot.getKey();
                FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);

                friend_request_id.add(dataSnapshot.getKey());
                friend_request_list.add(friendRequest);

                friendRequestRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                int friendReqIndex = friend_request_id.indexOf(dataSnapshot.getKey());

                if (friendReqIndex > -1) {

                    friend_request_id.remove(friendReqIndex);
                    friend_request_list.remove(friendReqIndex);

                    //friendRecyclerAdapter.notifyDataSetChanged();
                    friendRequestRecyclerAdapter.notifyItemRemoved(friendReqIndex);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
