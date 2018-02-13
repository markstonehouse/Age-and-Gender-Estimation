package com.example.mark.estimation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.example.mark.estimation.MainMenu.getTag;

/**
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender EstimateFaceFragment - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */
public class MainActivity extends AppCompatActivity
        implements CaptureImageFragment.CaptureImageListener,
        MultipleFacesFragment.OnFaceSelectedListener {

    private static final String TAG = getTag();

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Bundle bundle = new Bundle();

    private final int RESULT_GALLERY = 0;
    private final int FRAGMENT_KEY = 1;

    private Bitmap selectedImage;

    private FaceDetection faceDetection;

    private Classifier classifier;
    private Executor executor = Executors.newSingleThreadExecutor();

    private static final int INPUT_SIZE = 100;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input_input";
    private static final String OUTPUT_NAME = "output/Softmax";

    private static final String MODEL_FILE = "file:///android_asset/model_100_v3.pb";
    private static final String LABEL_FILE = "file:///android_asset/model_100_labels_v3.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        faceDetection = new FaceDetection(this);
        initTensorFlowAndLoadModel();

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
    }   // onCreate

    /** Initialise and load tensorflow model used for performing DNN. */
    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
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
    }   // initTensorFlowAndLocalModel()

    /** Launches new gallery intent to select image to import. */
    private void selectNewImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_GALLERY);
    }   // selectNewImage()

    /** Get selected image chosen for import. */
    private Bitmap getSelectedImage() {
        return selectedImage;
    }   // getSelectedImage()

    /** Pass in imported image and extract detected face from image. */
    private void getFacesFromImage(Bitmap importedImage) {
        ArrayList<Bitmap> foundFaces = faceDetection.detectFacesInImage(importedImage);

        if (foundFaces.size() != 0) {
            if (foundFaces.size() == 1) {
                processImage(foundFaces.get(0));
            }
            if (foundFaces.size() > 1) {
                fragmentMultipleFaces(foundFaces);
            }
        }
    }   // getFaceFromImage()

    /** Retrieves bitmap image of face, passes through DNN and passes results to EstimateFaceFragment. */
    private void processImage(Bitmap face) {
        Bitmap scaleFace = Bitmap.createScaledBitmap(face, INPUT_SIZE, INPUT_SIZE, false);

        List<Classifier.Recognition> results = classifier.recognizeImage(scaleFace);

        fragmentEstimateFace(face, results.toString());
    }   // processImage()

    /** Gallery intent on result, handles response from gallery and image imported. */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_GALLERY && resultCode == RESULT_OK && data != null) {
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (selectedImage != null) {
                getFacesFromImage(getSelectedImage());
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
        processImage(selectedFace);
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
        processImage(selectedFace);
    }   // onFaceSelected()

    /** Creates new EstimateFaceFragment - displays image and results from DNN. */
    private void fragmentEstimateFace(Bitmap data, String results) {
        EstimateFaceFragment fragment = new EstimateFaceFragment();

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

