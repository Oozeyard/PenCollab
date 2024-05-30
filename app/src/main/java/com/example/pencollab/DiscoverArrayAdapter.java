package com.example.pencollab;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pencollab.Activity.DiscoverActivity;
import com.example.pencollab.Activity.PreviewActivity;
import com.example.pencollab.Activity.ProfileActivity;
import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;

import java.util.ArrayList;

public class DiscoverArrayAdapter extends RecyclerView.Adapter<DiscoverArrayAdapter.ViewHolder> {
    private final ArrayList<Drawing> values;
    private final DrawingDAO drawingDAO;
    private final UserDAO userDAO;

    public DiscoverArrayAdapter(Context context, ArrayList<Drawing> values) {
        this.values = values;
        AppDatabase db = DatabaseHolder.getInstance(context.getApplicationContext());
        this.drawingDAO = db.drawingDAO();
        this.userDAO = db.userDAO();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drawing drawing = values.get(position);

        Context context = holder.itemView.getContext();

        holder.text_title_drawing.setText(drawing.getTitle());
        holder.text_owner_drawing.setText(userDAO.getUserByID(drawing.getOwnerId()).getUsername());

        // Display the drawing
        DrawingView drawingView = new DrawingView(context, null);
        drawingView.setSize(drawing.getWidth(), drawing.getHeight());
        drawingView.fromJSON(drawing.getDrawingData());
        holder.image_artwork.addView(drawingView.getDrawingPreview());

        holder.itemView.setOnClickListener(v -> {
            User user = userDAO.getUserByID(drawing.getOwnerId());

            Intent intent = new Intent(context, PreviewActivity.class);
            intent.putExtra("DrawingID", drawing.getId());
            intent.putExtra("UserID", user.getId());

            if (context instanceof DiscoverActivity || context instanceof ProfileActivity) intent.putExtra("isDicoverActivity", true);
            else intent.putExtra("isDicoverActivity", false);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout image_artwork;
        TextView text_title_drawing;
        TextView text_owner_drawing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_artwork = itemView.findViewById(R.id.image_artwork);
            text_title_drawing = itemView.findViewById(R.id.text_title_drawing);
            text_owner_drawing = itemView.findViewById(R.id.text_owner_drawing);
        }
    }
}
