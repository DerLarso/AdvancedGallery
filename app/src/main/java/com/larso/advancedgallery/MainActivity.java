package com.larso.advancedgallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.*;

import com.larso.advancedgallery.activities.SettingsActivity;
import com.larso.advancedgallery.activities.ShowCategory;

public class MainActivity extends AppCompatActivity {

    private LinearLayout categoryButtonContainer;

    private LinearLayout deleteCategoryButtonContainer;
    private static final String PREFS_NAME = "MyAdvancedGallaryPrefs";
    private static final String SETTINGS_KEY = "settings";
    private static final String CATEGORIES_KEY = "categories";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadAndApplySettings();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoryButtonContainer = findViewById(R.id.category_button_container);
        deleteCategoryButtonContainer = findViewById(R.id.delete_category_button_container);
        loadCategories();
    }

    private void loadAndApplySettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = prefs.getInt(SETTINGS_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }
    public void openSettings(View view){
        GoToSettings();
    }

    private void GoToSettings(){
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }
    public void createNewCategory(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Neue Kategorie:");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoryName = input.getText().toString();
                if (!categoryName.isEmpty()) {
                    addCategoryButton(categoryName);
                    saveCategories();
                }
            }
        });
        builder.setNegativeButton("Zurück", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addCategoryButton(String categoryName) {
        if(!categoryName.equals("\uD83D\uDDD1")){
            Button newCategoryButton = new Button(this);
            newCategoryButton.setText(categoryName);
            newCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoToCategory(categoryName);
                }
            });
            categoryButtonContainer.addView(newCategoryButton);
            Button newDeleteCategoryButton = new Button(this);
            newDeleteCategoryButton.setText("\uD83D\uDDD1");
            newDeleteCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCategory(categoryName);
                }
            });
            deleteCategoryButtonContainer.addView(newDeleteCategoryButton);
        }
    }
    private void deleteCategory(String categoryName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kategorie löschen?");
        builder.setMessage("Bist du sicher, dass du die Kategorie " + categoryName + " löschen willst?");
        builder.setPositiveButton("Ja!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Button categoryButtonToDelete = null;
                Button deleteButtonToDelete = null;

                for(int i = 0; i < categoryButtonContainer.getChildCount(); i++){
                    View child = categoryButtonContainer.getChildAt(i);
                    if(child instanceof Button && ((Button) child).getText().toString().equals(categoryName)){
                        categoryButtonToDelete = (Button) child;
                        View nextChild = deleteCategoryButtonContainer.getChildAt(i);
                        if(nextChild instanceof Button && ((Button) nextChild).getText().toString().equals("\uD83D\uDDD1")){
                            deleteButtonToDelete = (Button) nextChild;
                        }
                        break;
                    }

                }
                if(categoryButtonToDelete != null){
                    categoryButtonContainer.removeView(categoryButtonToDelete);
                }
                if(deleteButtonToDelete != null){
                    deleteCategoryButtonContainer.removeView(deleteButtonToDelete);
                }

                saveCategories();
            }
        });
        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        builder.show();
    }
    private void GoToCategory(String categoryName){
        Intent intent = new Intent(getApplicationContext(), ShowCategory.class);
        intent.putExtra("categoryName", categoryName);
        startActivity(intent);
    }
    private void loadCategories() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> categories = prefs.getStringSet(CATEGORIES_KEY, new HashSet<>());
        for (String categoryName : categories) {addCategoryButton(categoryName);
        }
    }

    private void saveCategories() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> categories = new HashSet<>();
        for (int i = 0; i < categoryButtonContainer.getChildCount(); i++) {
            View child = categoryButtonContainer.getChildAt(i);
            if (child instanceof Button) {
                categories.add(((Button) child).getText().toString());
            }
        }
        for (int i = 0; i < deleteCategoryButtonContainer.getChildCount(); i++) {
            View child = deleteCategoryButtonContainer.getChildAt(i);
            if (child instanceof Button) {
                categories.add(((Button) child).getText().toString());
            }
        }

        editor.putStringSet(CATEGORIES_KEY, categories);
        editor.apply();
    }
}



