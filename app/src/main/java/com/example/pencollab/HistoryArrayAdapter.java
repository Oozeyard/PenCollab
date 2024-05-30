package com.example.pencollab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pencollab.Activity.DrawingActivity;
import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.HistoryDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.History;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryArrayAdapter extends RecyclerView.Adapter<HistoryArrayAdapter.ViewHolder> {
    private final ArrayList<History> values;
    private final DrawingDAO drawingDAO;
    private final HistoryDAO historyDAO;

    public HistoryArrayAdapter(Context context, ArrayList<History> values) {
        this.values = values;
        AppDatabase db = DatabaseHolder.getInstance(context.getApplicationContext());
        this.drawingDAO = db.drawingDAO();
        this.historyDAO = db.historyDAO();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = values.get(position);

        Context context = holder.itemView.getContext();

        Date drawingDate = history.getCreationDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        holder.text_title_drawing.setText(dateFormat.format(drawingDate));
        holder.text_owner_drawing.setVisibility(View.GONE);

        // Clear previous drawing views
        holder.image_artwork.removeAllViews();

        // Display the drawing
        DrawingView drawingView = new DrawingView(context, null);
        drawingView.setSize(history.getWidth(), history.getHeight());
        drawingView.fromJSON(history.getDrawingData());
        holder.image_artwork.addView(drawingView.getDrawingPreview());

        holder.itemView.setOnClickListener(v -> {
            Drawing drawing = drawingDAO.getDrawingByID(history.Did);

            // Update drawing
            drawing.setDrawingData(history.getDrawingData());
            drawingDAO.updateDrawing(drawing);
            Toast.makeText(context, R.string.history_updated, Toast.LENGTH_LONG).show();

            // Return to the drawing
            Intent intent = new Intent(context, DrawingActivity.class);
            intent.putExtra("DrawingID", drawing.getId());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.delete)
                    .setMessage(R.string.delete_ask)
                    .setPositiveButton("YES", (dialog, which) -> {
                        // Delete to the database
                        historyDAO.deleteHistory(history);

                        // Delete to the list
                        values.remove(history);
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
