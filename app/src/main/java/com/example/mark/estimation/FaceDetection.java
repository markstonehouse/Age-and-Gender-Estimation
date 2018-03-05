package com.example.mark.estimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.mark.estimation.MainMenu.getTag;

/*
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender EstimateFaceFragment - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */

/**
 * Face detection object is responsible for the identifying of faces within a given image/mat.
 * If face found then returns identifiedFaces array.
 */
public class FaceDetection {

    private static final String TAG = getTag();

    private static final int JAVA_DETECTOR = 0;
    private static final int NATIVE_DETECTOR = 1;

    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    private int mDetectorType = JAVA_DETECTOR;
    private String[] mDetectorName;

    private float mRelativeFaceSize = 0.2f;
    private int mAbsoluteFaceSize = 0;

    public FaceDetection(Context c) {
        Context context = c;

        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        System.loadLibrary("detection_based_tracker");

        try {
            // load cascade file from application resources
            InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (mJavaDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * detectFacesInImage is responsible for converting bitmap to mat, retrieving found faces from
     * detect faces and creating a array of bitmap extracted faces.
     */
    public ArrayList<Bitmap> detectFacesInImage(Bitmap bitmap) {

        Mat mGray = new Mat (bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap, mGray);

        Rect[] identifiedFaces = detectFaces(mGray);

        ArrayList<Bitmap> bitmapsArray = new ArrayList();

        for (int i = 0; i < identifiedFaces.length; i++) {
            bitmapsArray.add(getBitmapOfFace(identifiedFaces[i], mGray));
        }

        return bitmapsArray;
    }   // detectFacesInImage()

    /** detectFacesInCapture is responsible for returning an array of rect extracted faces. */
    public Rect[] detectFacesInCapture(Mat mGray) {
        return detectFaces(mGray);
    }   // detectFacesInCapture()

    /** detectFaces takes a mat and performs a face detection before returning any identified faces. */
    private Rect[] detectFaces(Mat mGray) {
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }

        MatOfRect foundFaces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)
                mJavaDetector.detectMultiScale(mGray, foundFaces, 1.1, 2, 2,
                        new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, foundFaces);

        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] identifiedFaces = foundFaces.toArray();

        return identifiedFaces;
    }   // detectFaces()

    /** getBitmapOfFace converts a mat to a bitmap. */
    private Bitmap getBitmapOfFace(Rect extractedFaces, Mat matFaceToExtract) {
        Mat matFace = matFaceToExtract.submat(extractedFaces);

        Bitmap faceAsBitmap = Bitmap.createBitmap(matFace.cols(), matFace.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matFace, faceAsBitmap);

        return faceAsBitmap;
    }   // getBitmapOfFace()
}

