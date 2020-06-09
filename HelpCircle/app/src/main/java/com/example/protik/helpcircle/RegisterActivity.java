package com.example.protik.helpcircle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_register;
    private TextInputLayout name, email, phone, pass, con_pass;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    String nameGet, emailGet, phoneGet, passwordGet, confirmPasswordGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(this);

        name = findViewById(R.id.text_name);
        email = findViewById(R.id.text_email);
        phone = findViewById(R.id.text_phone);
        pass = findViewById(R.id.text_password);
        con_pass = findViewById(R.id.text_con_password);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btn_register) {
            registerUser();
        }
    }

    private void registerUser() {

        nameGet = name.getEditText().getText().toString().trim();
        emailGet = email.getEditText().getText().toString().trim();
        phoneGet = phone.getEditText().getText().toString().trim();
        passwordGet = pass.getEditText().getText().toString().trim();
        confirmPasswordGet = con_pass.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(emailGet)) {
            Toast.makeText(getApplicationContext(),"Please Enter Email..!!",Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(passwordGet)) {
            Toast.makeText(getApplicationContext(),"Please Enter Password..!!",Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(confirmPasswordGet)) {
            Toast.makeText(getApplicationContext(),"Please Re-Enter Password..!!",Toast.LENGTH_SHORT).show();
        }

        if (passwordGet.equals(confirmPasswordGet)) {

            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(emailGet, passwordGet).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Registered Successfully..!!",Toast.LENGTH_SHORT).show();

                        accountSetup();

                    } else {
                        progressDialog.dismiss();

                        String errorMsg = task.getException().getMessage();

                        Toast.makeText(getApplicationContext(),"Failed to Register..!!\nERROR : " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else {
            Toast.makeText(getApplicationContext(), "Password doesn't Match..!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void accountSetup() {

        mFirestore = FirebaseFirestore.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", nameGet);
        userMap.put("email", emailGet);
        userMap.put("phone", phoneGet);
        userMap.put("user_id", user_id);

        mFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(RegisterActivity.this, "Account is set up", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, "ERROR: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    allow read, write: if request.auth != null;
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        //finish();
//        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }
}
