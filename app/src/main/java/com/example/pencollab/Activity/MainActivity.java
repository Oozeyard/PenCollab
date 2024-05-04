package com.example.pencollab.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.GalleryArrayAdapter;
import com.example.pencollab.Picture;
import com.example.pencollab.R;
import com.example.pencollab.DataBase.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    LinearLayout draw_layout, join_layout, discover_layout, view_profile_layout;
    Button premium_button;
    Boolean isregistered;
    TextView profile_button, txt_user_name, txt_user_status;

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

        profile_button = findViewById(R.id.profile_button);
        txt_user_name = findViewById(R.id.txt_user_name);
        txt_user_status = findViewById(R.id.txt_user_status);

        // Build Database
        // WARNING : ENLEVER .allowMainThreadQueries() ET METTRE UN THREAD A PART
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().build();
        // WARNING : ENLEVER .allowMainThreadQueries() ET METTRE UN THREAD A PART

        // Get DAO
        UserDAO userDAO = db.userDAO();
        DrawingDAO drawingDAO = db.drawingDAO();

        //userDAO.nukeTable(); ca nettoie bien

        // Check if the user is registered or not
        User currentUser = new User();
        long id = userDAO.insertUser(currentUser);

        User Michel = new User("Michel", "6969!", "xxxvoley@gmail.com", false);
        long MichelID = userDAO.insertUser(Michel);

        // Unregistered / registered
        if (currentUser.getId() == 0) {
            profile_button.setText(R.string.login);
            join_layout.setVisibility(View.GONE);
            isregistered = false;
            txt_user_status.setText(R.string.unregistered);
        }
        else {
            profile_button.setText(R.string.view_profile);
            join_layout.setVisibility(View.VISIBLE);
            isregistered = true;
            txt_user_status.setText(R.string.registered);
        }

        txt_user_name.setText(currentUser.getUsername());

        // Handle activity change
        draw_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, DrawingActivity.class)));
        join_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, PreviewActivity.class)));
        discover_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, DiscoverActivity.class)));

        if (isregistered) view_profile_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        else view_profile_layout.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, LoginActivity.class)));

        premium_button.setOnClickListener(v -> this.startActivity(new Intent(MainActivity.this, GetPremiumActivity.class)));


        // Set up gallery list
        RecyclerView recyclerView_pictures = findViewById(R.id.container_gallery);
        recyclerView_pictures.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Picture> pictures = new ArrayList<>();
        long userId = currentUser.getId();
        pictures.add(new Picture("Couilles"));
        pictures.add(new Picture("Chattes"));
        pictures.add(new Picture("Bite"));
        pictures.add(new Picture("Nibard"));
        pictures.add(new Picture("Gros Seins"));
        pictures.add(new Picture("Les Tchoutches"));
        pictures.add(new Picture("Masha allah"));
        pictures.add(new Picture("Amen !"));

        GalleryArrayAdapter list_pictures = new GalleryArrayAdapter(pictures);

        recyclerView_pictures.setAdapter(list_pictures);
    }
}