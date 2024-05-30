package com.example.pencollab.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.DrawingUserDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.DiscoverArrayAdapter;
import com.example.pencollab.R;

import java.util.ArrayList;
import java.util.List;

public class JoinActivity extends AppCompatActivity {

    Context context;
    ImageView back_arrow;
    ListView join_list;
    EditText search_bar;
    UserDAO userDAO;
    DrawingDAO drawingDAO;
    DrawingUserDAO drawingUserDAO;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.join_activity);

        // Get context
        context = getApplicationContext();

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(context);

        // Get DAO
        userDAO = db.userDAO();
        drawingDAO = db.drawingDAO();
        drawingUserDAO = db.drawingUserDAO();

        back_arrow = findViewById(R.id.back_arrow);
        search_bar = findViewById(R.id.search_bar);

        // Set up discover list
        join_list = findViewById(R.id.join_list);

        // Get current & add all shared drawings
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            long userId = intent.getLongExtra("UserID", 0);
            currentUser = userDAO.getUserByID(userId);
            List<Long> sharedDrawingIDs = drawingUserDAO.getSharedDrawingID(currentUser.getId());
            if (!sharedDrawingIDs.isEmpty()) {
                ArrayList<Drawing> sharedDrawing = (ArrayList<Drawing>) drawingDAO.getDrawingsByIDs(sharedDrawingIDs);
                join_list.setAdapter((ListAdapter) new DiscoverArrayAdapter(this, sharedDrawing));
            }
        }

        join_list.setOnItemClickListener((parent, view, position, id) -> {
            Drawing drawing = (Drawing) parent.getItemAtPosition(position);
            User user = userDAO.getUserByID(drawing.getOwnerId());

            Intent intent1 = new Intent(context, PreviewActivity.class);
            intent1.putExtra("DrawingID", drawing.getId());
            intent1.putExtra("UserID", user.getId());
            intent1.putExtra("isDiscoverActivity", false);
            startActivity(intent1);
            finish();
        });

        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(context, MainActivity.class));
            finish();
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            // Go back to the Main activity
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

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