package com.sakhawat.youtubedownloader.tools;

import android.content.Context;
import android.content.SharedPreferences;
import static com.sakhawat.youtubedownloader.tools.Constants.PREF_NAME;

public class PrefManager {
    private int PRIVATE_MODE = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public PrefManager(Context context) {
        this.context = context;
    }

    public void set(String key, boolean value) {
        editor = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void set(String key, String value) {
        editor = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void set(String key, int value) {
        editor = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getBoolean(key, false);
    }

    public String getString(String key) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getString(key, "");
    }

    public int getInt(String key) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        return pref.getInt(key, 0);
    }

    public void clear(){
        editor = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
        editor.clear();
        editor.commit();
    }
}
