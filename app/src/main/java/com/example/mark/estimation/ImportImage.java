package com.example.mark.estimation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender Estimation - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */
public class ImportImage extends AppCompatActivity {

    private final String TAG = "Estimation";

    private Rect[] extractedFace;

    /**
     * Object that handles face detection - OpenCV
     */
    private FaceDetection faceDetection;

    private final int RESULT_GALLERY = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_image);

        faceDetection = new FaceDetection(this);

        /**
         * Button to return back to MainMenu
         */
        final ImageButton btn_backButton = findViewById(R.id.btn_backButton);
        btn_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); // btn_backButton

        selectNewImage();
    }   // onCreate

    private void selectNewImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_GALLERY && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Mat imgMatRgba = new Mat (bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(bitmap, imgMatRgba);

            Mat imgMatGray = new Mat (bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
            Utils.bitmapToMat(bitmap, imgMatGray);

            extractedFace = faceDetection.detectFaces(imgMatGray);

            if (extractedFace.length == 0) {
                Toast toast = Toast.makeText(this, "No face detected. Please select another image.", Toast.LENGTH_LONG);
                toast.show();

                selectNewImage();

            } else if (extractedFace.length > 1) {
                long[] matFaces = new long[extractedFace.length];
                for (int i = 0; i < extractedFace.length; i++) {
                    Mat matFace = imgMatRgba.submat(extractedFace[i]);
                    matFaces[i] = matFace.getNativeObjAddr();
                }
                Intent intentMultipleFaces = new Intent(ImportImage.this, MultipleFaces.class);
                intentMultipleFaces.putExtra("multipleFaces", matFaces);
                startActivity(intentMultipleFaces);
                finish();
            } else if (extractedFace.length == 1){
                Mat matFace = imgMatRgba.submat(extractedFace[0]);

                long getFace = matFace.getNativeObjAddr();

                Intent intent = new Intent(ImportImage.this, EstimateFace.class);
                intent.putExtra("extractedFace", getFace);
                startActivity(intent);
                finish();
            } else {
                Log.e(TAG, "Error performing detection for face.");
                Toast toast = Toast.makeText(this, "Error performing face detection.", Toast.LENGTH_LONG);
                toast.show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }   // onActivityResult
}
