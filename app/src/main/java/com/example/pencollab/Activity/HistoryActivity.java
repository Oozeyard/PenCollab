package com.example.pencollab.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import com.example.pencollab.DiscoverArrayAdapter;
import com.example.pencollab.HistoryArrayAdapter;
import com.example.pencollab.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    Context context;
    ImageView back_arrow;
    ListView history_list;
    HistoryDAO historyDAO;
    DrawingDAO drawingDAO;
    ArrayList<History> historylist;
    ArrayList<Drawing> drawingList;

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

        // Get current & add all shared drawings
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            long userID = intent.getLongExtra("UserID", 0);
            long drawingID = intent.getLongExtra("DrawingID", 0);
            historylist = new ArrayList<>(historyDAO.getHistoryByDidUid(userID, drawingID));
        }

        // Set up history list
        history_list.setAdapter((ListAdapter) new HistoryArrayAdapter(this, historylist));
        history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                History history = (History) parent.getItemAtPosition(position);
                Drawing drawing = drawingDAO.getDrawingByID(history.Did);

                // Update drawing
                drawing.setDrawingData(history.getDrawingData());
                drawingDAO.updateDrawing(drawing);
                Toast.makeText(getApplicationContext(), R.string.history_updated, Toast.LENGTH_LONG).show();

                // Return to the drawing
                Intent intent = new Intent(getApplicationContext(), DrawingActivity.class);
                intent.putExtra("DrawingID", drawing.getId());
                startActivity(intent);
                finish();
            }
        });

        history_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                History history = (History) parent.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setTitle(R.string.delete)
                        .setMessage(R.string.delete_ask)
                        .setPositiveButton("YES", (dialog, which) -> {
                            // Delete to the database
                            historyDAO.deleteHistory(history);

                            // Delete to the list
                            historylist.remove(history);
                            ((BaseAdapter) history_list.getAdapter()).notifyDataSetChanged();
                            dialog.dismiss();
                        })
                        .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();

                return true;
            }
        });
        // back arrow
        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(context, MainActivity.class));
            finish();
        });

    }
}