package com.example.pencollab.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pencollab.GalleryArrayAdapter;
import com.example.pencollab.Picture;
import com.example.pencollab.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout draw_layout, join_layout, discover_layout, view_profile_layout;
    Button premium_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_menu_activity);

        // Get view
        draw_layout = findViewById(R.id.container_draw);
        join_layout = findViewById(R.id.container_join);
        discover_layout = findViewById(R.id.container_discover);
        view_profile_layout = findViewById(R.id.container_view_profile);
        premium_button = findViewById(R.id.premium_button);

        // Handle activity change
        draw_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, DrawingActivity.class)));
        join_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, PreviewActivity.class)));
        discover_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, DiscoverActivity.class)));
        view_profile_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        premium_button.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, GetPremiumActivity.class)));


        RecyclerView recyclerView_pictures = findViewById(R.id.container_gallery);
        recyclerView_pictures.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Picture> pictures = new ArrayList<>();
        pictures.add(new Picture("Bite", "Art"));
        pictures.add(new Picture("Couilles", "Art2"));

        GalleryArrayAdapter list_pictures = new GalleryArrayAdapter(pictures);

        recyclerView_pictures.setAdapter(list_pictures);
    }
}