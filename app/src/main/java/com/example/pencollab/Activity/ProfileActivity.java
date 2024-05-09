package com.example.pencollab.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.R;

public class ProfileActivity extends AppCompatActivity {

    ImageView back_arrow;

    TextView user_textView, status_textView;
    LinearLayout button_log_out;

    User currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_activity);

        back_arrow = findViewById(R.id.back_arrow);
        user_textView = findViewById(R.id.txt_user_name);
        status_textView = findViewById(R.id.txt_user_status);
        button_log_out = findViewById(R.id.container_button_log_out);

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(getApplicationContext());

        // Get DAO
        UserDAO userDAO = db.userDAO();
        DrawingDAO drawingDAO = db.drawingDAO();

        // Get current user
        currentUser = userDAO.getCurrentUser();

        user_textView.setText(currentUser.getUsername());

        if (currentUser.getPremium()) status_textView.setText(R.string.premium);
        else status_textView.setText(R.string.not_premium);

        button_log_out.setOnClickListener(v -> {
            currentUser.setCurrentUser(false);
            userDAO.updateUser(currentUser);

            currentUser = userDAO.getUserByID(1); // default user
            currentUser.setCurrentUser(true);
            userDAO.updateUser(currentUser);

            nice();
        });

        back_arrow.setOnClickListener(v -> {
            nice();
        });
    }

    private void nice() {
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}