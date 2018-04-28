package com.example.mark.estimation;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.mark.estimation.MainMenu.getTag;

/*
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender EstimateFaceFragment - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */
public class MainActivity extends AppCompatActivity
        implements CaptureImageFragment.CaptureImageListener,
        MultipleFacesFragment.OnFaceSelectedListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Bundle bundle = new Bundle();

    private final int RESULT_GALLERY = 0;
    private final int FRAGMENT_KEY = 1;

    /* Global variable selectedImage used to store image of face for estimation. */
    private Bitmap selectedImage;

    private FaceDetection faceDetection;

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static final int INPUT_SIZE = 100;
    private static final int IMAGE_MEAN = 114;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input_input";
    private static final String OUTPUT_NAME = "output/Softmax";

    private static final String GENDER_MODEL_FILE = "file:///android_asset/model_100_v3.pb";
    private static final String GENDER_LABEL_FILE = "file:///android_asset/gender_labels.txt";
//    private static final String AGE_LABEL_FILE = "file:///android_asset/age_labels.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        initTensorFlowAndLoadModel();

        faceDetection = new FaceDetection(this);

        if (getIntent().hasExtra("menuOption")) {
            switch (getIntent().getStringExtra("menuOption")) {
                case "captureImage":
                    fragmentCaptureImage();
                    break;
                case "importImage" :
                    selectNewImage();
                    break;
            }
        }   // menuOptions
    }   // onCreate()

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            GENDER_MODEL_FILE,
                            GENDER_LABEL_FILE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }   // initTensorFlowAndLoadModel()

    /** Launches new gallery intent to select image to import. */
    public void selectNewImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_GALLERY);
    }   // selectNewImage()

    /** Get selected image chosen for import. */
    private Bitmap getSelectedImage() {
        return selectedImage;
    }   // getSelectedImage()

    /** Set selected image chosen for import. */
    private void setSelectedImage(Bitmap value) { this.selectedImage = value; }   // setSelectedImage()

    /** Pass in imported image and process detected faces in image. */
    private void getFacesFromImage() {
        ArrayList<Bitmap> foundFaces = faceDetection.detectFacesInImage(getSelectedImage());

        if (foundFaces.size() != 0) {
            if (foundFaces.size() == 1) {
                processImage(foundFaces.get(0));
            }
            if (foundFaces.size() > 1) {
                fragmentMultipleFaces(foundFaces);
            }
        } else {
            Toast.makeText(this, "No faces found in image", Toast.LENGTH_SHORT).show();
            finish();
        }
    }   // getFaceFromImage()

    /** Retrieves bitmap image of face, passes through DNN and passes results to EstimateFaceFragment. */
    private void processImage(Bitmap face) {
        Bitmap scaledFace = Bitmap.createScaledBitmap(face, INPUT_SIZE, INPUT_SIZE, false);

        List<Classifier.Recognition> results = classifier.recognizeImage(scaledFace);

        fragmentEstimateFace(face, results.toString());
    }   // processImage()

    /** Gallery intent on result, handles response from gallery and image imported. */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_GALLERY && resultCode == RESULT_OK && data != null) {
            try {
                setSelectedImage(MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()));
                Log.d("Testing", "Path: " + data.getData().getPath());
                getFacesFromImage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }

        if (requestCode == FRAGMENT_KEY && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getParcelableExtra("selectedFace");
        }
    }   // onActivityResult()

    /** Create new CaptureImageFragment - allows user to capture new image using device's camera. */
    private void fragmentCaptureImage() {
        CaptureImageFragment fragment = new CaptureImageFragment();

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }   // fragmentCaptureImage()

    /** Callback for CaptureImageFragment - returns bitmap of image captured by user. */
    @Override
    public void newImageCaptured(Bitmap selectedFace) {
        setSelectedImage(selectedFace);
        getFacesFromImage();
    }   // newImageCaptured()

    /** Creates new MultipleFacesFragment - makes user select a single face from an image. */
    private void fragmentMultipleFaces(ArrayList<Bitmap> data) {
        MultipleFacesFragment fragment = new MultipleFacesFragment();

        bundle.putParcelableArrayList("bitmapArray", data);
        fragment.setArguments(bundle);

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }   // fragmentMultiplesFaces()

    /** Callback for MultipleFacesFragment - returns the selected face from an image containing
     ** multiple faces. */
    @Override
    public void onFaceSelected(Bitmap selectedFace) {
        setSelectedImage(selectedFace);
        getFacesFromImage();
    }   // onFaceSelected()

    /** Creates new EstimateFaceFragment - displays image and results from DNN. */
    private void fragmentEstimateFace(Bitmap data, String results) {
        EstimationResultsFragment fragment = new EstimationResultsFragment();

        bundle.putParcelable("bitmap", data);
        bundle.putString("results", results);
        fragment.setArguments(bundle);

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }   // fragmentEstimateFace()
}

