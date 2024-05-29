package com.example.pencollab;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;

import java.util.ArrayList;

public class DiscoverArrayAdapter extends ArrayAdapter<Drawing> {
    private final Context context;
    public DiscoverArrayAdapter(Context context, ArrayList<Drawing> drawings){
        super(context, R.layout.discover_display, drawings);
        this.context = context;
    }

    public View getView(int position, View discoverView, ViewGroup parent){
        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(context);

        // Get DAO
        UserDAO userDAO = db.userDAO();

        if (discoverView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            discoverView = inflater.inflate(R.layout.discover_display, parent, false);
        }

        FrameLayout image_artwork = discoverView.findViewById(R.id.image_artwork);
        TextView text_title_drawing = discoverView.findViewById(R.id.text_title_drawing);
        TextView text_owner_drawing = discoverView.findViewById(R.id.text_owner_drawing);

        Drawing currentDrawing = getItem(position);

        // Display the drawing
        DrawingView drawingView = new DrawingView(getContext(), null);
        drawingView.setSize(currentDrawing.getWidth(), currentDrawing.getHeight());
        drawingView.fromJSON(currentDrawing.getDrawingData());
        image_artwork.addView(drawingView.getDrawingPreview());

        text_title_drawing.setText(currentDrawing.getTitle());
        text_owner_drawing.setText(userDAO.getUserByID(currentDrawing.getOwnerId()).getUsername());

        return discoverView;
    }

}
