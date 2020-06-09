package com.example.protik.helpcircle;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendRequestRecyclerAdapter extends RecyclerView.Adapter<FriendRequestRecyclerAdapter.ViewHolder> {

    private Button confirmBtn, deleteBtn;
    private FirebaseFirestore mFireStore;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendReqReceivedDatabase;
    private FirebaseUser mCurrentUser;

    public List<FriendRequest> friend_request_list;

    public FriendRequestRecyclerAdapter(List<FriendRequest> friend_request_list) {
        this.friend_request_list = friend_request_list;
    }

    @NonNull
    @Override
    public FriendRequestRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_list_item, parent, false);
        return new FriendRequestRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestRecyclerAdapter.ViewHolder holder, int position) {

        String date = friend_request_list.get(position).getDate();
        String userId = friend_request_list.get(position).getUser_id();

        holder.setText(userId);
    }

    @Override
    public int getItemCount() {
        return friend_request_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView requestNameView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setText(final String userData){

//            Toast.makeText(mView.getContext(), "UID: " + userData, Toast.LENGTH_SHORT).show();
            requestNameView = mView.findViewById(R.id.request_name);
            confirmBtn = mView.findViewById(R.id.btn_confirm);
            deleteBtn = mView.findViewById(R.id.btn_delete);

            mFireStore = FirebaseFirestore.getInstance();
            mFireStore.collection("Users").document(userData).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        String userName = task.getResult().getString("name");
                        requestNameView.setText(userName);

                    } else {
                        String error = task.getException().getMessage();
                    }
                }
            });

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mView.getContext(), "confirm", Toast.LENGTH_SHORT).show();
                    confirmFriend(mView, userData);
                }
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mView.getContext(), "delete", Toast.LENGTH_SHORT).show();
                    deleteFriendReq(mView, userData);
                }
            });
        }
    }

    private void deleteFriendReq(final View view, final String uId) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendReqReceivedDatabase = FirebaseDatabase.getInstance().getReference().child("Received_Friend_Req");

        mFriendRequestDatabase.child(mCurrentUser.getUid()).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFriendRequestDatabase.child(uId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFriendReqReceivedDatabase.child(mCurrentUser.getUid()).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(view.getContext(), "Request Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void confirmFriend(final View view, final String uId) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendReqReceivedDatabase = FirebaseDatabase.getInstance().getReference().child("Received_Friend_Req");

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
                                                Toast.makeText(view.getContext(), "Request Accepted", Toast.LENGTH_SHORT).show();
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
