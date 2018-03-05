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
import android.widget.Toast;

/*
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender EstimateFaceFragment - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */
public class MainMenu extends AppCompatActivity {

    /* PERMISSIONS_GRANTED constant used for providing a value during permission check. */
    private static final int PERMISSIONS_GRANTED = 0;

    /* MENU_OPTION constant used for determining which fragment to load in MainActivity. */
    private final String MENU_OPTION = "menuOption";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        /* Call permission check on app startup. */
        checkForPermissions();

        /* Button to start new Capture activity. */
        final Button btn_captureImage = findViewById(R.id.btn_captureImage);
        btn_captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOption = new Intent(MainMenu.this, MainActivity.class);
                menuOption.putExtra(MENU_OPTION, "captureImage");
                startActivity(menuOption);
            }
        }); // btn_captureImage

        /* Button to start new ImportImage activity. */
        final Button btn_importImage = findViewById(R.id.btn_importImage);
        btn_importImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuOption = new Intent(MainMenu.this, MainActivity.class);
                menuOption.putExtra(MENU_OPTION, "importImage");
                startActivity(menuOption);
            }
        }); // btn_importImage
    }   // onCreate()

    /** Check user has allowed application to use services needing defined permissions. */
    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_GRANTED);
        } else {
            // Activate menu buttons
            activateMenuButtons(true, true);
        }
    }   // checkForPermissions()

    /** Handles responses from user granting or denying application permissions. */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_GRANTED: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        activateMenuButtons(true, false);
                    }
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        activateMenuButtons(false, true);
                    }
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED &&
                            grantResults[1] == PackageManager.PERMISSION_DENIED) {
                        Toast toast = Toast.makeText(this, "Application requires permissions.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                return;
            }
        }
    }   // onRequestPermissionsResult()

    /** Activates and colours menu buttons upon granting of app permissions. */
    private void activateMenuButtons(boolean cameraPermission, boolean storagePermission) {
        if (cameraPermission) {
            final Button btn_captureImage = findViewById(R.id.btn_captureImage);
            btn_captureImage.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btn_captureImage.setEnabled(true);

        }
        if (storagePermission) {
            final Button btn_importImage = findViewById(R.id.btn_importImage);
            btn_importImage.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            btn_importImage.setEnabled(true);
        }
    }   // activateMenuButtons()

    /** @return Tag name "EstimateFaceFragment" when printing to Logcat - accessible throughout the app. */
    protected static String getTag() {
        return "Estimation";
    }   // getTag()
}
