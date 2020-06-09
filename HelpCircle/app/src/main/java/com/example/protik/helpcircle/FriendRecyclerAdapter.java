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

import java.util.List;

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder> {

    private Button unfriendBtn;
    private FirebaseFirestore mFireStore;
    private DatabaseReference mFriendDatabase;
    private FirebaseUser mCurrentUser;

    public List<Friend> friend_list;

    public FriendRecyclerAdapter(List<Friend> friend_list) {
        this.friend_list = friend_list;
    }

    @NonNull
    @Override
    public FriendRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendRecyclerAdapter.ViewHolder holder, int position) {

        String date = friend_list.get(position).getDate();
        String userId = friend_list.get(position).getUser_id();

        holder.setText(userId);
    }

    @Override
    public int getItemCount() {
        return friend_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView nameView, phoneView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setText(final String userData) {

            nameView = mView.findViewById(R.id.nameId);
            phoneView = mView.findViewById(R.id.mobileId);
            unfriendBtn = mView.findViewById(R.id.unfriendBtnId);

            mFireStore = FirebaseFirestore.getInstance();
            mFireStore.collection("Users").document(userData).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        String userName = task.getResult().getString("name");
                        String phoneData = task.getResult().getString("phone");

                        //Toast.makeText(mView.getContext(), "name: "+ userName, Toast.LENGTH_SHORT).show();
                        nameView.setText(userName);
                        phoneView.setText(phoneData);
                    } else {
                        String error = task.getException().getMessage();
                    }
                }
            });

            unfriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFriend(mView, userData);
                }
            });
        }
    }

    public void deleteFriend(final View view, final String uId) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        mFriendDatabase.child(mCurrentUser.getUid()).child(uId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFriendDatabase.child(uId).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(view.getContext(), "deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
