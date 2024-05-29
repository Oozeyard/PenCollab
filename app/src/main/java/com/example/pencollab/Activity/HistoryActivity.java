package com.example.pencollab.Activity;

import android.content.Context;
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
import com.example.pencollab.DataBase.DAO.HistoryDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.History;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.R;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    Context context;
    ImageView back_arrow;
    ListView discover_list;
    HistoryDAO historyDAO;
    List<History> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.history_activity);

        // Get context
        context = getApplicationContext();

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(context);

        // Get DAO
        historyDAO = db.historyDAO();

        back_arrow = findViewById(R.id.back_arrow);

        // Get current & add all shared drawings
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            long userID = intent.getLongExtra("UserID", 0);
            long drawingID = intent.getLongExtra("DrawingID", 0);
            historyList = historyDAO.getHistoryByDidUid(userID, drawingID);
        }

        // back arrow
        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(context, MainActivity.class));
            finish();
        });

    }
}