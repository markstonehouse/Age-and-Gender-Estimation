package com.example.mark.estimation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender Estimation - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */
public class MainMenu extends AppCompatActivity {

    private static final String TAG = "Estimation";

    private static final int PERMISSIONS_GRANTED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        checkForPermissions();

        /**
         * Button to start new CaptureImage activity
         */
        final Button btn_captureImage = findViewById(R.id.btn_captureImage);
        btn_captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(MainMenu.this, CaptureImage.class);
                startActivity(captureIntent);
            }
        }); // btn_captureImage

        /**
         * Button to start new ImportImage activity
         */
        final Button btn_importImage = findViewById(R.id.btn_importImage);
        btn_importImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent importIntent = new Intent(MainMenu.this, ImportImage.class);
                startActivity(importIntent);

            }
        }); // btn_importImage
    }   // onCreate

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_GRANTED);
        } else {
            final Button btn_captureImage = findViewById(R.id.btn_captureImage);
            btn_captureImage.setEnabled(true);

            final Button btn_importImage = findViewById(R.id.btn_importImage);
            btn_importImage.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_GRANTED: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        final Button btn_captureImage = findViewById(R.id.btn_captureImage);
                        btn_captureImage.setEnabled(true);
                    }
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        final Button btn_importImage = findViewById(R.id.btn_importImage);
                        btn_importImage.setEnabled(true);
                    }
                }
                return;
            }
        }
    }
}
