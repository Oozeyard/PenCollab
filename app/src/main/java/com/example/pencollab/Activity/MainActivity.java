package com.example.pencollab.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.GalleryArrayAdapter;
import com.example.pencollab.R;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LinearLayout draw_layout, join_layout, discover_layout, view_profile_layout;
    Button premium_button;
    Boolean isregistered;
    TextView profile_button, txt_user_name, txt_user_status;

    //private AppDatabase db;
    //private UserDAO userDAO;
    //private DrawingDAO drawingDAO;

    User currentUser;

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

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(getApplicationContext());

        // Get DAO
        UserDAO userDAO = db.userDAO();
        DrawingDAO drawingDAO = db.drawingDAO();

        //userDAO.nukeTable(); ca nettoie bien

        // Get currentUser
        currentUser = userDAO.getCurrentUser();

        // Check if the user is null
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setCurrentUser(true);
            userDAO.insertUser(currentUser);
        }

        Log.d("User", "Current User: " + currentUser.getUsername() + ", Mail: " + currentUser.getEmail());

        long currentUserId = currentUser.getId();

        // Unregistered / registered
        if (currentUserId <= 1 ) { // no registered (id <= 1 : default user)
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

        txt_user_name.setText(currentUser.getUsername()); // write the user name

        // Handle activity change
        draw_layout.setOnClickListener(v -> startNewActivity(DrawingActivity.class));
        join_layout.setOnClickListener(v -> startNewActivity(PreviewActivity.class));
        discover_layout.setOnClickListener(v -> startNewActivity(DiscoverActivity.class));

        if (isregistered) view_profile_layout.setOnClickListener(v -> startNewActivity(ProfileActivity.class));
        else view_profile_layout.setOnClickListener(v -> startNewActivity(LoginActivity.class));

        premium_button.setOnClickListener(v -> startNewActivity(GetPremiumActivity.class));


        // Set up gallery list
        RecyclerView recyclerView_pictures = findViewById(R.id.container_gallery);
        recyclerView_pictures.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Drawing> drawings = new ArrayList<>(drawingDAO.getDrawingsByOwnerID(currentUserId));
        recyclerView_pictures.setAdapter(new GalleryArrayAdapter(drawings, this));


        List<User> userList = userDAO.getAll();

        for (User user : userList) Log.d("User", "ID: " + user.getId() + ", Name: " + user.getUsername() + ", Mail: " + currentUser.getEmail());
    }

    // /!\ à voir /!\
    private void startNewActivity(Class<?> classActivity) {
        this.startActivity(new Intent(MainActivity.this, classActivity));
        finish();
    }
}