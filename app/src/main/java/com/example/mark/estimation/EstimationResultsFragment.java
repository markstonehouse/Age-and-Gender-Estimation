package com.example.mark.estimation;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender EstimateFaceFragment - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */

/**
 * EstimationResultsFragment takes the extracted face and results from DNN modules and displays
 * them in the fragment.
 */
public class EstimationResultsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }   // onCreate()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_estimate_face, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final ImageView imageViewResult = rootView.findViewById(R.id.imageViewResult);
        final TextView textViewResult = rootView.findViewById(R.id.textViewResult);
        final Button btn_newEstimation = rootView.findViewById(R.id.btn_newEstimation);

        Bitmap face = getArguments().getParcelable("bitmap");
        String results = getArguments().getString("results");

        if (face != null && results != "") {
            imageViewResult.setImageBitmap(face);
            textViewResult.setText(results);
        } else {
            textViewResult.setText("Error loading results.");
        }

        /* Button to head back to the MainMenu to begin to estimation. */
        btn_newEstimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }   // onCreateView()

    @Override
    public void onPause() {
        super.onPause();

        getActivity().finish();
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().finish();
    }
}
