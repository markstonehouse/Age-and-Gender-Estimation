package com.example.mark.estimation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender Estimation - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */
public class CaptureImage extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "Estimation";

    private Rect[] extractedFace;

    private Scalar FACE_RECT_COLOR;

    private Mat mRgba;
    private Mat mGray;

    private CameraBridgeViewBase mOpenCvCameraView;

    /**
     * Object that handles face detection - OpenCV
     */
    private FaceDetection faceDetection;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };  // BaseLoaderCallback

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_capture_image);

        faceDetection = new FaceDetection(this);

        FACE_RECT_COLOR = getFaceRectColour();

        mOpenCvCameraView = findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

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

        /**
         * Button to extract face from detector
         */
        Button btn_extractFace = findViewById(R.id.btn_extractFace);
        btn_extractFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateSingleFace(extractedFace);
            }
        });
    }   // onCreate

    private void validateSingleFace(Rect[] extractedFace) {
        if (extractedFace == null) {
            Toast toast = Toast.makeText(this, "No face detected.", Toast.LENGTH_SHORT);
            toast.show();
        } else if (extractedFace.length == 1) {
            // Extract submat of face from camera material
            Mat matFace = mRgba.submat(extractedFace[0]);

            long getFace = matFace.getNativeObjAddr();

            Intent intent = new Intent(CaptureImage.this, EstimateFace.class);
            intent.putExtra("extractedFace", getFace);
            startActivity(intent);
            finish();
        } else {
            Toast toast = Toast.makeText(this, "Multiple faces detected: " + extractedFace.length, Toast.LENGTH_SHORT);
            toast.show();
        }
    }   // validateSingleFace

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        extractedFace = faceDetection.detectFaces(mRgba, mGray);

        for (int i = 0; i < extractedFace.length; i++) {
            Imgproc.rectangle(mRgba, extractedFace[i].tl(), extractedFace[i].br(), FACE_RECT_COLOR, 3);
        }

        return mRgba;
    }

    private Scalar getFaceRectColour() {

        int colorAccent = ContextCompat.getColor(this, R.color.colorAccent);

        int R = (colorAccent >> 16) & 0xff;
        int G = (colorAccent >>  8) & 0xff;
        int B = (colorAccent      ) & 0xff;

        return new Scalar(R, G, B);
    }
}
