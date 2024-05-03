package com.example.pencollab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryArrayAdapter extends RecyclerView.Adapter<GalleryArrayAdapter.ViewHolder> {
    private final ArrayList<Picture> values;

    public GalleryArrayAdapter(ArrayList<Picture> values){
        this.values = values;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryArrayAdapter.ViewHolder holder, int position) {
        Picture picture = values.get(position);
        holder.title.setText(picture.getTitle());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title_drawing);
        }
    }
}