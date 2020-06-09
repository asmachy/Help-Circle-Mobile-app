package com.example.protik.helpcircle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView user_list_view;
    private List<User> user_list;
    UserRecyclerAdapter userRecyclerAdapter;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        searchView = findViewById(R.id.searchId);

        user_list = new ArrayList<>();
        user_list_view = findViewById(R.id.user_list_view);

        userRecyclerAdapter = new UserRecyclerAdapter(user_list);
        user_list_view.setLayoutManager(new LinearLayoutManager(this));
        user_list_view.setAdapter(userRecyclerAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        User user = doc.getDocument().toObject(User.class);
                        user_list.add(user);

                        userRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_SHORT).show();
                userRecyclerAdapter.getFilter().filter(s);
                return false;
            }
        });
    }
}