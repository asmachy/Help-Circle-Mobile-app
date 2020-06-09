package com.example.protik.helpcircle;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class NavigationClass implements NavigationView.OnNavigationItemSelectedListener {

    Context context;

    public NavigationClass(Context applicationContext) {
        context = applicationContext;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.home) {
            Toast.makeText(context, "Home", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId() == R.id.notifications)
        {
            Toast.makeText(context, "Notification", Toast.LENGTH_SHORT).show();
        }

        else if(item.getItemId() == R.id.searchFriends)
        {
            Toast.makeText(context, "Search Friends", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, AllUsersActivity.class);
            context.startActivity(intent);
        }

        else if(item.getItemId() == R.id.friends)
        {
            Toast.makeText(context, "Friends", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, FriendsActivity.class);
            context.startActivity(intent);

        }

        else if(item.getItemId() == R.id.requests)
        {
            Toast.makeText(context, "Friend Request", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, FriendRequestActivity.class);
            context.startActivity(intent);
        }

        else if(item.getItemId() == R.id.logOut)
        {
            FirebaseAuth firebaseAuth;
            firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.signOut();
            Toast.makeText(context,"Logged Out..!!",Toast.LENGTH_SHORT).show();
            //context.finish();
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }

        return true;
    }
}
