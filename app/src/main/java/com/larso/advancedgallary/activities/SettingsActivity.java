package com.larso.advancedgallary.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.*;

import com.larso.advancedgallary.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyAdvancedGallaryPrefs";
    public static final String SETTINGS_KEY = "settings";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadAndApplySettings();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void saveSettings(int themeMode){
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(SettingsActivity.SETTINGS_KEY, themeMode);
        editor.apply();
    }
    public void loadAndApplySettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = prefs.getInt(SETTINGS_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }
    public void setDarkmode(View view) {
        saveSettings(AppCompatDelegate.MODE_NIGHT_YES);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    public void setLightmode(View view) {
        saveSettings(AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void closeSettings(View view) {
        finish();
    }
}
