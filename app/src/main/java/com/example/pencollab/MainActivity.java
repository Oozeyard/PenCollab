package com.example.pencollab;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.preview_activity);

        /*ListView listView_pictures = findViewById(R.id.container_gallery);
        registerForContextMenu(listView_pictures);

        ArrayList<Picture> pictures = new ArrayList<>();
        pictures.add(new Picture("Bite", "Art"));
        pictures.add(new Picture("Couilles", "Art2"));

        GalleryArrayAdapter list_pictures = new GalleryArrayAdapter(this, pictures);

        listView_pictures.setAdapter(list_pictures);*/
    }
}