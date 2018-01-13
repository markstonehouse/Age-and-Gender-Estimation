package com.example.mark.estimation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender Estimation - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */
public class MultipleFaces extends AppCompatActivity {

    private final String TAG = "Estimation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_faces);

        Intent getIntent = getIntent();
        final long[] multipleFaces = getIntent.getLongArrayExtra(("multipleFaces"));

        ArrayList<MultipleFaceRow> bitmaps = new ArrayList<MultipleFaceRow>();

        for (long faceAsLong : multipleFaces) {
            Mat extractedFace = new Mat(faceAsLong);

            Bitmap imgBitmap = Bitmap.createBitmap(extractedFace.cols(), extractedFace.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(extractedFace, imgBitmap);
            bitmaps.add(new MultipleFaceRow(imgBitmap));
        }

        CustomAdapter customAdapter = new CustomAdapter(MultipleFaces.this, bitmaps);
        ListView multipleFacesList = findViewById(R.id.multipleFacesList);
        multipleFacesList.setAdapter(customAdapter);

        /**
         * employee list view to take json data and populate app with records
         */
        final ListView employeeList = findViewById(R.id.multipleFacesList);
        employeeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long getFace = multipleFaces[position];

                Intent intent = new Intent(MultipleFaces.this, EstimateFace.class);
                intent.putExtra("extractedFace", getFace);
                startActivity(intent);
                finish();
            }
        });
    }   // onCreate
}
