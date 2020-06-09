package com.example.protik.helpcircle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private ImageView iv_notifications;
    private ImageView iv_search;
    private ImageView iv_location;
    private ImageView btn_help_here;
    private ImageView btn_panic;


    DrawerLayout dl;
    ActionBarDrawerToggle abdt;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

//        mToolbar = findViewById(R.id.main_page_toolbar);
//        setSupportActionBar(mToolbar);
        setUpToolbar();
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Help Circle");
        }

        iv_notifications = findViewById(R.id.iv_notifications);
        iv_search = findViewById(R.id.iv_search);
        iv_location = findViewById(R.id.iv_location);
        btn_help_here = findViewById(R.id.btn_help_here);
        btn_panic = findViewById(R.id.btn_panic);

        iv_notifications.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_location.setOnClickListener(this);
        btn_help_here.setOnClickListener(this);
        btn_panic.setOnClickListener(this);

//        setOptionsActivity();

        NavigationView nav_view = findViewById(R.id.nav_view);
        NavigationClass navigationClass = new NavigationClass(MainActivity.this);
        nav_view.setNavigationItemSelectedListener(navigationClass);
    }

    public void setUpToolbar() {
        dl = findViewById(R.id.dl);
        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        abdt = new ActionBarDrawerToggle(this,dl,toolbar,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (abdt.onOptionsItemSelected(item)) {
            return true;
        }
        MenuInflaterClass obj = new MenuInflaterClass(item, MainActivity.this);
        return obj.yo();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_notifications) {
            Toast.makeText(MainActivity.this, "notification", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.iv_search) {
            Toast.makeText(MainActivity.this, "search", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AllUsersActivity.class));
        }
        else if (v.getId() == R.id.iv_location) {
            //Toast.makeText(MainActivity.this, "location", Toast.LENGTH_SHORT).show();
            checkLocationPermission();
        }
        else if (v.getId() == R.id.btn_help_here) {
            Toast.makeText(MainActivity.this, "help here", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HelpHereActivity.class));
        }
        else if (v.getId() == R.id.btn_panic) {
            //Toast.makeText(MainActivity.this, "panic", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PanicActivity.class));
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location permission")
                        .setMessage("Permission is needed to get your current location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        999);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        999);
            }
        } else {
            startActivity(new Intent(this, MapActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 999: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(new Intent(this, MapActivity.class));

                } else {
                    Toast.makeText(this, "Permission denied! Couldn't get your location", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
