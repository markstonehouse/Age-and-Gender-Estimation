package com.example.mark.estimation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/*
 * Author: Mark Stonehouse
 * Student ID: 15085629
 * Project: Age & Gender EstimateFaceFragment - MMU Final Year Project
 * Supervisor: Dr Moi Hoon Yap
 * Version: 1.0
 */

/**
 * CustomAdapater is used in the MultipleFacesFragment activity.
 * Responsible for the handling and styling of faces extracted from an image.
 */
public class CustomAdapter extends ArrayAdapter<MultipleFaceRow> {

    public CustomAdapter(Context context, ArrayList<MultipleFaceRow> bitmaps) {
        super(context, 0, bitmaps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MultipleFaceRow extractedFaceRow = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customadapter_row_layout, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.multipleFaceImageView);
        imageView.setImageBitmap(extractedFaceRow.faceImage);

        return convertView;
    }
}

