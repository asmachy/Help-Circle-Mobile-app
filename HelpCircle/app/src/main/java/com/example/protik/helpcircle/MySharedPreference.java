package com.example.protik.helpcircle;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {

    private SharedPreferences preferences;
    private static final String PANIC_ACTIVE = "panic_active";
    private static final String SP_DB = "help_circle";
    private Context context;

    MySharedPreference(Context context){
        this.context = context;
    }

    public void savePanicMode(boolean value){
        preferences = context.getSharedPreferences(SP_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PANIC_ACTIVE, value);
        editor.apply();
    }

    public boolean getPanicMode(){
        preferences = context.getSharedPreferences(SP_DB, Context.MODE_PRIVATE);
        return preferences.getBoolean(PANIC_ACTIVE, false);
    }
}
