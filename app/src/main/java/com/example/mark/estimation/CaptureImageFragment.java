package com.example.mark.estimation;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/*
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender EstimateFaceFragment - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */

/**
 * Capture image activity uses the OpenCV library to capture an image and perform a face detection
 * on said image. A coloured rectangle will appear around any detected faces. Upon clicking of the
 * 'Estimate' button the face will be extracted and passed to the 'EstimationResultsFragment'
 * to display the estimated results.
 */
public class CaptureImageFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2{

    private final String TAG = getTag();

    /* Array of rect used to story any extracted faces found from facial detection. */
    private Rect[] extractedFace;

    /* Colour of rectangle around the face upon detection of face. */
    private Scalar FACE_RECT_COLOR;

    /* Mats uses to display camera views. */
    private Mat mRgba;
    private Mat mGray;

    private CameraBridgeViewBase mOpenCvCameraView;

    /* Object that handles face detection - passes camera mat to object and returns detected faces .*/
    private FaceDetection faceDetection;

    private boolean cameraBack = true;

    /* Check OpenCV has loaded properly and begin camera view. */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext()) {
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

    /* CaptureImageListener used to pass captured image from camera back to parent activity. */
    public CaptureImageListener fragmentCallback;
    public interface CaptureImageListener {
        void newImageCaptured(Bitmap selectedFace);
    }   // newImageCaptured

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            fragmentCallback = (CaptureImageListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFaceSelectedListener.");
        }
    }   // onAttach()

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }   // onCreate()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_capture_image, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mOpenCvCameraView = rootView.findViewById(R.id.cameraView_captureImage);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        faceDetection = new FaceDetection(getActivity());

        FACE_RECT_COLOR = getFaceRectColour();  /* Get accent colour of app for face rectangle. */

        /* Button used to alternate between front and back camera upon interaction. */
        final ImageButton btn_changeCamera = rootView.findViewById(R.id.btn_changeCamera);
        btn_changeCamera.setSelected(cameraBack);
        btn_changeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraBack) {
                    cameraBack = false;
                    btn_changeCamera.setSelected(cameraBack);
                    mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
                    mOpenCvCameraView.disableView();
                } else {
                    cameraBack = true;
                    btn_changeCamera.setSelected(cameraBack);
                    mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
                    mOpenCvCameraView.disableView();
                }

                mOpenCvCameraView.enableView();
            }
        }); // btn_changeCamera

        /* Button to return back to MainMenu. */
        final ImageButton btn_backButton = rootView.findViewById(R.id.btn_backButton_captureImage);
        btn_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        }); // btn_backButton

        /* Button to extract face from detector. */
        Button btn_extractFace = rootView.findViewById(R.id.btn_extractFace);
        btn_extractFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateSingleFace(extractedFace);
            }
        }); // btn_extractFace

        return rootView;
    }   // onCreateView()

    /** Performs a check on the amount of values stored in 'extractedFace' array to ensure only
     *  1 face is present, else a message will appear alerting the user to try again. */
    private void validateSingleFace(Rect[] extractedFace) {
        if (extractedFace.length == 1) {
            Mat matFace = mRgba.submat(extractedFace[0]); // Extract submat of face from camera material

            imageCaptured(convertMatToBitmap(matFace));

        } else if (extractedFace.length > 1) {
            Toast.makeText(getActivity(), "Multiple faces detected: "
                    + extractedFace.length, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "No face detected.", Toast.LENGTH_SHORT).show();
        }
    }   // validateSingleFace()

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }   // onPause()

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, getActivity(), mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }   // onResume()

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }   // onDestroy()

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
    }   // onCameraViewStarted()

    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }   // onCameraViewStopped()

    /** onCameraFrame takes each camera frame and passes it through face detection, any faces
     *  returned will have a coloured rectangle drawn round them. */
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        /* If front camera is being used flip the camera to create mirror effect. */
        if (cameraBack == false) {
            Core.flip(mRgba, mRgba, Core.ROTATE_180);
            Core.flip(mGray, mGray, Core.ROTATE_180);
        }

        /* Get any detected faces and store them in extractedFace. */
        extractedFace = faceDetection.detectFacesInCapture(mGray);

        // On each face within extractedFace draw a rectangle around the face for camera preview.
        for (int i = 0; i < extractedFace.length; i++) {
            Imgproc.rectangle(mRgba, extractedFace[i].tl(), extractedFace[i].br(), FACE_RECT_COLOR, 3);
        }

        return mRgba;
    }   // onCameraFrame()

    /** Get accent colour from app preferences and create new scalar for FACE_RECT_COLOR. */
    private Scalar getFaceRectColour() {
        int colorAccent = ContextCompat.getColor(getActivity(), R.color.colorAccent);

        int R = (colorAccent >> 16) & 0xff;
        int G = (colorAccent >>  8) & 0xff;
        int B = (colorAccent      ) & 0xff;

        return new Scalar(R, G, B);
    }   // getFaceRectColour()

    /** Converts any passed mat to bitmap. */
    private Bitmap convertMatToBitmap(Mat extractedFace) {
        Bitmap extractedFaceAsBitmap =
                Bitmap.createBitmap(extractedFace.cols(), extractedFace.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(extractedFace, extractedFaceAsBitmap);

        return extractedFaceAsBitmap;
    }   // convertMatToBitmap()

    /** After image is captured pass back to parent activity and remove current fragment. */
    public void imageCaptured(Bitmap faceAsBitmap) {
        fragmentCallback.newImageCaptured(faceAsBitmap);
        getActivity().getFragmentManager().popBackStack();
    }   // onListItemClick()
}
