package com.example.pencollab.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.R;

public class PreviewActivity extends AppCompatActivity {

    ImageView back_arrow;
    TextView text_tile, subtitle_text, date_text, size_text;
    Intent intent;
    AppDatabase database;
    DrawingDAO drawingDAO;
    UserDAO userDAO;
    Drawing currentDrawing;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.preview_activity);

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(getApplicationContext());

        // Get DAO
        userDAO = db.userDAO();
        drawingDAO = db.drawingDAO();

        back_arrow = findViewById(R.id.back_arrow);
        text_tile = findViewById(R.id.text_title);
        subtitle_text = findViewById(R.id.subtitle_text);
        date_text = findViewById(R.id.created_date_text);
        size_text = findViewById(R.id.size_text);

        intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            long drawingID = intent.getLongExtra("DrawingID", 0);
            long userID = intent.getLongExtra("UserID", 0);
            currentDrawing = drawingDAO.getDrawingByID(drawingID);
            currentUser = userDAO.getUserByID(userID);
            UpdateInformation();
        }

        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    void UpdateInformation() {
        
    }
}