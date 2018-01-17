package com.example.mark.estimation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DnnTest extends AppCompatActivity  {

    private static final String TAG = "Estimation";

    private final int RESULT_GALLERY = 0;

    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "output";

    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();

    private TextView textViewResult;
    private ImageView imageViewResult;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };  // BaseLoaderCallback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dnn_test);

        imageViewResult = findViewById(R.id.imageViewResult);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        initTensorFlowAndLoadModel();

        selectNewImage();

        Button btn_selectImage = findViewById(R.id.btn_selectImage);
        btn_selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNewImage();
            }
        });
    }

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

            Mat imgMatRgba = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
            Utils.bitmapToMat(bitmap, imgMatRgba);

            bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

            imageViewResult.setImageBitmap(bitmap);

            final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

            textViewResult.setText(results.toString());

        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }   // onActivityResult

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
    }
}
