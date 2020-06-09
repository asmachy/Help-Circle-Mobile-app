package com.example.protik.helpcircle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfile extends AppCompatActivity {

    private ImageView profileImage, editBtn;
    private TextView nameTxt, phoneTxt, emailTxt;

    private FirebaseUser mCurrentUser;
    private FirebaseFirestore mFirestore;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mFirestore = FirebaseFirestore.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser == null) {
            Intent intent = new Intent(MyProfile.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        profileImage = findViewById(R.id.imageView);
        editBtn = findViewById(R.id.editText);
        nameTxt = findViewById(R.id.my_name);
        phoneTxt = findViewById(R.id.mobile_no);
        emailTxt = findViewById(R.id.email_no);

        String user_id = mCurrentUser.getUid();
        docRef = mFirestore.collection("Users").document(user_id);

        retrieveData();
    }

    private void retrieveData() {

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    String name = task.getResult().getString("name");
                    String phone = task.getResult().getString("phone");
                    String email = task.getResult().getString("email");
                    //Toast.makeText(MyProfile.this, "Name: " + name, Toast.LENGTH_SHORT).show();

                    nameTxt.setText(name);
                    phoneTxt.setText(phone);
                    emailTxt.setText(email);

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(MyProfile.this, "ERROR: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
