package com.example.mark.estimation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EstimateFaceFragment extends Fragment {

    private final String TAG = getTag();

    private TextView textViewResult;
    private ImageView imageViewResult;
    private Button btn_newEstimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }   // onCreate()

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_estimate_face, container, false);

        imageViewResult = rootView.findViewById(R.id.imageViewResult);
        textViewResult = rootView.findViewById(R.id.textViewResult);
        btn_newEstimation = rootView.findViewById(R.id.btn_newEstimation);

        Bitmap face = getArguments().getParcelable("bitmap");
        String results = getArguments().getString("results");

        imageViewResult.setImageBitmap(face);
        textViewResult.setText(results);

        /** Button to head back to the MainMenu to begin to estimation. */
        btn_newEstimation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }   // onCreateView()

}
