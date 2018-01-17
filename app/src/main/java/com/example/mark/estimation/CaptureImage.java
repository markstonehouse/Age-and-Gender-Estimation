package com.example.mark.estimation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import static com.example.mark.estimation.MainMenu.getTag;

/**
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender Estimation - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */

/**
 * CaptureImage activity uses the OpenCV camera listener to capture an image and pass it to the
 * face detection object that it creates. A coloured rectangle will display around the detected
 * face. Upon click of the 'Estimate' button the face will be extracted from the image and passed
 * to the EstimationActivity.
 */
public class CaptureImage extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    // Get tag name "Estimation" to use when printing to Logcat
    private static final String TAG = getTag();

    // Array of rect to store all the detected faces to allow extraction
    private Rect[] extractedFace;

    // Colour of rectangle around the face upon detection of face
    private Scalar FACE_RECT_COLOR;

    // Mats uses to display camera views
    private Mat mRgba;
    private Mat mGray;

    private CameraBridgeViewBase mOpenCvCameraView;

    // Object that handles face detection - passes camera mat to object and returns detected faces
    private FaceDetection faceDetection;

    // Check OpenCV has loaded properly and begin camera view
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

        FACE_RECT_COLOR = getFaceRectColour();  // Get accent colour of app for face rectangle

        mOpenCvCameraView = findViewById(R.id.cameraView_captureImage);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        /**
         * Button to return back to MainMenu
         */
        final ImageButton btn_backButton = findViewById(R.id.btn_backButton_captureImage);
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
        }); // btn_extractFace
    }   // onCreate

    /**
     * Performs a check on the value stored in extractedFace to see that only 1 face is present
     */
    private void validateSingleFace(Rect[] extractedFace) {
        if (extractedFace.length == 1) {
            Mat matFace = mRgba.submat(extractedFace[0]); // Extract submat of face from camera material

            long getFace = matFace.getNativeObjAddr();  // Convert matFace to long

            Intent intent = new Intent(CaptureImage.this, EstimateFace.class);
            intent.putExtra("extractedFace", getFace);
            startActivity(intent);
            finish();
        } else if (extractedFace.length > 1) {
            Toast toast = Toast.makeText(this, "Multiple faces detected: "
                    + extractedFace.length, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, "No face detected.", Toast.LENGTH_SHORT);
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

    /**
     * OpenCV method that performs code inside on each frame of the camera
     */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        // Get any detected faces and store them in extractedFace
        extractedFace = faceDetection.detectFaces(mGray);

        // On each face within extractedFace draw a rectangle around the face
        for (int i = 0; i < extractedFace.length; i++) {
            Imgproc.rectangle(mRgba, extractedFace[i].tl(), extractedFace[i].br(), FACE_RECT_COLOR, 3);
        }

        return mRgba;
    }

    /**
     * Get accent colour of app and create new scalar for FACE_RECT_COLOR
     */
    private Scalar getFaceRectColour() {
        int colorAccent = ContextCompat.getColor(this, R.color.colorAccent);

        int R = (colorAccent >> 16) & 0xff;
        int G = (colorAccent >>  8) & 0xff;
        int B = (colorAccent      ) & 0xff;

        return new Scalar(R, G, B);
    }
}
