package com.example.pencollab.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.HistoryDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.History;
import com.example.pencollab.HistoryArrayAdapter;
import com.example.pencollab.R;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    Context context;
    ImageView back_arrow;
    RecyclerView history_list;
    HistoryDAO historyDAO;
    DrawingDAO drawingDAO;
    ArrayList<History> historylist;


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
        drawingDAO = db.drawingDAO();

        back_arrow = findViewById(R.id.back_arrow);
        history_list = findViewById(R.id.history_list);
        history_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Get current & add all shared drawings
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            long userID = intent.getLongExtra("UserID", 0);
            long drawingID = intent.getLongExtra("DrawingID", 0);
            historylist = new ArrayList<>(historyDAO.getHistoryByDidUid(userID, drawingID));
        }

        // Set up history list
        history_list.setAdapter(new HistoryArrayAdapter(this, historylist));

        // back arrow
        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(context, MainActivity.class));
            finish();
        });

    }
}