package com.example.mark.estimation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

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
}
