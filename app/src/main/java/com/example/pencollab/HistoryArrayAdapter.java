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
import com.example.pencollab.DataBase.History;
import com.example.pencollab.DataBase.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryArrayAdapter extends ArrayAdapter<History> {
    private final Context context;
    public HistoryArrayAdapter(Context context, ArrayList<History> histories){
        super(context, R.layout.discover_display, histories);
        this.context = context;
    }

    public View getView(int position, View discoverView, ViewGroup parent){

        if (discoverView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            discoverView = inflater.inflate(R.layout.discover_display, parent, false);
        }

        FrameLayout image_artwork = discoverView.findViewById(R.id.image_artwork);
        TextView text_title_drawing = discoverView.findViewById(R.id.text_title_drawing);
        TextView text_owner_drawing = discoverView.findViewById(R.id.text_owner_drawing);
        text_owner_drawing.setVisibility(View.GONE);

        History currentHistory = getItem(position);

        // Display the drawing
        DrawingView drawingView = new DrawingView(getContext(), null);
        drawingView.setSize(currentHistory.getWidth(), currentHistory.getHeight());
        drawingView.fromJSON(currentHistory.getDrawingData());
        image_artwork.addView(drawingView.getDrawingPreview());

        Date drawingDate = currentHistory.getCreationDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        text_title_drawing.setText(dateFormat.format(drawingDate));

        return discoverView;
    }

}
