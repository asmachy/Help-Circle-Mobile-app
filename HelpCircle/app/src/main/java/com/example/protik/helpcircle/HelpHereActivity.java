package com.example.protik.helpcircle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class HelpHereActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap map;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_here);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.help_here_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            firestore = FirebaseFirestore.getInstance();
            getFriendsData();
        } else {
            Toast.makeText(this, "Map fragment null", Toast.LENGTH_SHORT).show();
        }
    }

    private void getFriendsData() {
        firestore.collection("users_location").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        UserLocationModel location = doc.getDocument().toObject(UserLocationModel.class);
                        addMarkers(location);
                    }
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);

            map.setOnMyLocationButtonClickListener(this);
        } else {
            checkLocationPermission();
        }
//        LatLng latLng = new LatLng(22.4741, 91.8076);
//        mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
//        mMap.animateCamera(CameraUpdateFactory.zoomIn());
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
    }

    public void checkLocationPermission() {
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
                            ActivityCompat.requestPermissions(HelpHereActivity.this,
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
    }

    private void addMarkers(UserLocationModel location){
        //Log.e("Location", location.getLatitude()+", "+location.getLatitude());
        map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                location.getLongitude())).title(location.getUserName()+"'s Location"));
        moveCamera(location);
    }

    private void moveCamera(UserLocationModel userLocation){
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);

        map.moveCamera(center);
        map.animateCamera(zoom);
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

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}
