package com.larso.advancedgallery.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.larso.advancedgallery.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.*;


public class ShowCategory extends AppCompatActivity {
    private LinearLayout imageTableContainer;
    public static final String PREFS_NAME = "MyAdvancedGallaryPrefs";
    public String THIS_CATEGORY = "";

    private ActivityResultLauncher<Intent> pickImageLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        THIS_CATEGORY = intent.getStringExtra("categoryName");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageTableContainer = findViewById(R.id.image_table_container);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri sourceUri = result.getData().getData();
                        if (sourceUri != null) {
                            String newFileName = copyImageToInternalStorage(sourceUri);
                            if (newFileName != null) {
                                addImageRow(newFileName);
                            }
                        }
                    }
                }
        );

        loadItems();
    }
    private String copyImageToInternalStorage(Uri uri){
        String fileName = "IMG_" + UUID.randomUUID().toString() + ".jpg";
        File categorydirection = new File(getFilesDir(), THIS_CATEGORY);
        if(!categorydirection.exists()){
            categorydirection.mkdirs();
        }

        File destinationFile = new File(categorydirection, fileName);
        try (InputStream in = getContentResolver().openInputStream(uri)) {
            OutputStream out = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            return fileName;
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Fehler beim Laden des Bildes", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    public void loadItems(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> imageFileName = prefs.getStringSet(THIS_CATEGORY, new HashSet<>());
        imageTableContainer.removeAllViews();
        for (String fileName : imageFileName) {
            addImageRow(fileName);
        }
    }
    private void addImageRow(String fileName) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setGravity(Gravity.CENTER_VERTICAL);
        rowLayout.setTag(fileName);

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(300,300);
        imageParams.setMargins(10,10,10,10);
        imageView.setLayoutParams(imageParams);

        File imageFile = new File(new File(getFilesDir(), THIS_CATEGORY), fileName);
        if(imageFile.exists()){
            imageView.setImageURI(Uri.fromFile(imageFile));
        }

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Button deleteButton = new Button(this);
        deleteButton.setText("\uD83D\uDDD1");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowCategory.this);
                builder.setTitle("Möchtest du das Bild wirklich löschen?");
                builder.setPositiveButton("Ja!", (dialog, which) -> {
                    imageFile.delete();
                    imageTableContainer.removeView(rowLayout);
                    saveItems();
                    Toast.makeText(ShowCategory.this, "Bild gelöscht", Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Nein", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();

            }
            });
        rowLayout.addView(imageView);
        rowLayout.addView(deleteButton);
        imageTableContainer.addView(rowLayout);
        saveItems();
    }
    public void saveItems() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> imageFileNames = new HashSet<>();
        for (int i = 0; i < imageTableContainer.getChildCount(); i++) {
            View child = imageTableContainer.getChildAt(i);
            if (child.getTag() != null) {
                imageFileNames.add(child.getTag().toString());
            }
        }
        editor.putStringSet(THIS_CATEGORY,imageFileNames);
        editor.apply();
    }


    public void createNewImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    public void closeCategory(View view) {
        finish();
    }
}
