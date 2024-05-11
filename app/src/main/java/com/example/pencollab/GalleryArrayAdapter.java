package com.example.pencollab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pencollab.Activity.DrawingActivity;
import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;

import java.util.ArrayList;

public class GalleryArrayAdapter extends RecyclerView.Adapter<GalleryArrayAdapter.ViewHolder> {
    private final ArrayList<Drawing> values;
    private final DrawingDAO drawingDAO;



    public GalleryArrayAdapter(ArrayList<Drawing> values, Context context){
        this.values = values;
        AppDatabase db = DatabaseHolder.getInstance(context.getApplicationContext());
        this.drawingDAO = db.drawingDAO();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryArrayAdapter.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Drawing drawing = values.get(position);
        holder.title.setText(drawing.getTitle());

        Drawing currentDrawing = values.get(position);

        // Display the drawing
        DrawingView drawingView = new DrawingView(holder.itemView.getContext(), null);
        drawingView.setSize(currentDrawing.getWidth(), currentDrawing.getHeight());
        drawingView.fromJSON(currentDrawing.getDrawingData());
        holder.drawing_preview.addView(drawingView.getDrawingPreview());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DrawingActivity.class);
            intent.putExtra("DrawingID", currentDrawing.getId());
            holder.itemView.getContext().startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(R.string.delete)
                    .setMessage(R.string.delete_ask)
                    .setPositiveButton("YES", (dialog, which) -> {
                        // Delete to the database
                        drawingDAO.deleteDrawing(currentDrawing);

                        // Delete to the list
                        values.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, values.size());
                        dialog.dismiss();
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        FrameLayout drawing_preview;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title_drawing);
            drawing_preview = itemView.findViewById(R.id.image_artwork);
        }
    }


}