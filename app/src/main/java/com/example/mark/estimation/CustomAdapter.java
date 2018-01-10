package com.example.mark.estimation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Author: Mark Stonehouse
 * Version: 1.0
 */
public class CustomAdapter extends ArrayAdapter<ExtractedFaceRow> {
    public CustomAdapter(Context context, ArrayList<ExtractedFaceRow> bitmaps) {
        super(context, 0, bitmaps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExtractedFaceRow extractedFaceRow = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.customadapter_row_layout, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.multipleFaceImageView);
        imageView.setImageBitmap(extractedFaceRow.faceImage);

        return convertView;
    }
}

