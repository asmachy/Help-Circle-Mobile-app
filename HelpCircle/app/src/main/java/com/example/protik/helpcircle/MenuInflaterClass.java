package com.example.protik.helpcircle;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

public class MenuInflaterClass {

    MenuItem item;
    Context context;

    public MenuInflaterClass(MenuItem menuItem, Context ctx) {
        context = ctx;
        item = menuItem;
    }

    public boolean yo() {

        if (item.getItemId() == R.id.my_profile) {
            Toast.makeText(context,"My Profile",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MyProfile.class);
            context.startActivity(intent);
        }
        else if (item.getItemId() == R.id.about) {
            Toast.makeText(context,"About",Toast.LENGTH_SHORT).show();
            Intent about = new Intent(context, AboutActivity.class);
            context.startActivity(about);
        }
//        else if (item.getItemId() == R.id.allUsers) {
//            Toast.makeText(context, "All Users", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(context, AllUsersActivity.class);
//            context.startActivity(intent);
//        }

        return true;
    }
}
