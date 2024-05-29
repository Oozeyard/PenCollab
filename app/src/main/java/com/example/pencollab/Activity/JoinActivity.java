package com.example.pencollab.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.DrawingUserDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.R;

import java.util.List;

public class JoinActivity extends AppCompatActivity {

    ImageView back_arrow;
    ListView discover_list;
    EditText search_bar;
    UserDAO userDAO;
    DrawingDAO drawingDAO;
    DrawingUserDAO drawingUserDAO;
    List<Drawing> drawings;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.join_activity);

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(getApplicationContext());

        // Get DAO
        userDAO = db.userDAO();
        drawingDAO = db.drawingDAO();
        drawingUserDAO = db.drawingUserDAO();

        back_arrow = findViewById(R.id.back_arrow);
        search_bar = findViewById(R.id.search_bar);

        // Get current & add all shared drawings
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            long userId = intent.getLongExtra("UserID", 0);
            currentUser = userDAO.getUserByID(userId);
            List<Long> sharedDrawingIDs = drawingUserDAO.getSharedDrawingID(currentUser.getId());
            if (!sharedDrawingIDs.isEmpty()) {
                List<Drawing> sharedDrawing = drawingDAO.getDrawingsByIDs(sharedDrawingIDs);
            }
        }

        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
}