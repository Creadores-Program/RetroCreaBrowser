package org.CreadoresProgram.RetroCreaBrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class ConfigManager {

    private static final String PREF_NAME = "config_pref";
    private final SharedPreferences prefs;

    public ConfigManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        save(editor);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        save(editor);
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        save(editor);
    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        save(editor);
    }

    public void clearAll() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        save(editor);
    }

    private void save(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}