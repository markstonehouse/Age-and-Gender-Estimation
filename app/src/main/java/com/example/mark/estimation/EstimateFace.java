package com.example.mark.estimation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import static com.example.mark.estimation.MainMenu.getTag;

/**
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender Estimation - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */
public class EstimateFace extends AppCompatActivity {

    private static final String TAG = getTag();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_face);

        Intent getIntent = getIntent();
        long extractedFaceAsLong = getIntent.getLongExtra("extractedFace", 0);
        Mat extractedFace = new Mat(extractedFaceAsLong);

        final ImageView imageView = findViewById(R.id.imageView);

        if (extractedFace.cols() == 0 && extractedFace.rows() == 0) {
            Toast toast = Toast.makeText(this, "Error handling images.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Bitmap extractFaceAsBitmap = Bitmap.createBitmap(extractedFace.cols(), extractedFace.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(extractedFace, extractFaceAsBitmap);

            imageView.setImageBitmap(extractFaceAsBitmap);
        }

        /**
         * Button to return back to MainMenu activity to begin to estimation
         */
        final Button btn_newEstimation = findViewById(R.id.btn_newEstimation);
        btn_newEstimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }   // onCreate
}
