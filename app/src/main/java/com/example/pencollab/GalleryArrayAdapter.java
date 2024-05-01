package com.example.pencollab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GalleryArrayAdapter extends ArrayAdapter<Picture> {

    private final Context context;

    public GalleryArrayAdapter(Context context, ArrayList<Picture> values){
        super(context, R.layout.gallery_display, values);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View cellView = convertView;
        if (cellView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cellView = inflater.inflate(R.layout.gallery_display, parent, false);
        }

        TextView title = cellView.findViewById(R.id.text_title_drawing);
        TextView desc = cellView.findViewById(R.id.text_subtitle_sketch);

        Picture picture = getItem(position);

        assert picture != null;

        title.setText(picture.getTitle());
        desc.setText(picture.getOther());

        return cellView;
    }
}
