package com.example.lab_rest.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lab_rest.model.User;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "lab_rest";
    private static final String KEY_ID = "key_id";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_ROLE = "key_role";
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    public SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void storeUser(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_ROLE, null)
        );
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null) != null;
    }
}
