package com.example.mark.estimation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class EstimateFace extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate_face);

        Intent getIntent = getIntent();
        long face = getIntent.getLongExtra("extractedFace", 0);
        Mat image = new Mat(face);
        Mat img = image.clone();

        final ImageView imageView = findViewById(R.id.imageView);

        if (img.cols() == 0 && img.rows() == 0) {
            Toast toast = Toast.makeText(this, "Error handling images.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(img, bitmap);

            imageView.setImageBitmap(bitmap);
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
