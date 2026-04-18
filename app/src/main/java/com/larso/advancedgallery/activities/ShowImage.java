package com.larso.advancedgallery.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.larso.advancedgallery.R;

import java.io.File;

public class ShowImage extends AppCompatActivity {
    private static final String PREFS_NAME = "MyAdvancedGallaryPrefs";
    private static final String SETTINGS_KEY = "settings";
    private ImageView imageView;
    private LinearLayout layout;
    private String THIS_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        THIS_IMAGE = intent.getStringExtra("file");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.showImage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = findViewById(R.id.imageView);
        imageView.setAdjustViewBounds(true);
        if(THIS_IMAGE != null){
            File image = new File(THIS_IMAGE);
            if (image.exists()){
                imageView.setImageURI(Uri.fromFile(image));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(params);
            }
        }
    }
    public void closeImageView(View view){
        finish();
    }
}
