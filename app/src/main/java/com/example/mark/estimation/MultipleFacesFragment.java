package com.example.mark.estimation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/*
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender EstimateFaceFragment - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */

/**
 * MultipleFacesFragment takes in an array of extracted faces and displays them in a list view.
 * The user is prompted to select an image that will be used for estimation.
 */
public class MultipleFacesFragment extends ListFragment {

    private ArrayList<MultipleFaceRow> multipleFaces;

    /* Once face has been selected it will be passed back to the parent activity. */
    public OnFaceSelectedListener fragmentCallback;
    public interface OnFaceSelectedListener {
        void onFaceSelected(Bitmap selectedFace);
    }   // onFaceSelectedListener

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            fragmentCallback = (OnFaceSelectedListener) activity;
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
        final View rootView = inflater.inflate(R.layout.fragment_multiple_faces, container, false);

        ArrayList<Bitmap> faces = getArguments().getParcelableArrayList("bitmapArray");

        multipleFaces = new ArrayList();

        for (Bitmap face : faces) {
            multipleFaces.add(new MultipleFaceRow(face));
        }

        return rootView;
    }   // onCreateView()

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CustomAdapter customAdapter = new CustomAdapter(getView().getContext(), multipleFaces);
        setListAdapter(customAdapter);
    }   // onActivityCreated()

    /** When list item is clicked of face it will be passed back to the parent activity. */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        fragmentCallback.onFaceSelected(multipleFaces.get(position).faceImage);
    }   // onListItemClick()
}
