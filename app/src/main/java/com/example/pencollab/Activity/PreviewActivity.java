package com.example.pencollab.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.DrawingView;
import com.example.pencollab.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PreviewActivity extends AppCompatActivity {

    Context context;
    ImageView back_arrow;
    TextView text_tile, subtitle_text, date_text, size_text, title_text;
    Button button_join;
    Intent intent;
    DrawingDAO drawingDAO;
    UserDAO userDAO;
    Drawing currentDrawing;
    User user, drawingUser;
    FrameLayout container_drawing;
    Boolean isProvideDiscoverActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.preview_activity);

        // Get context
        context = getApplicationContext();

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(context);

        // Get DAO
        userDAO = db.userDAO();
        drawingDAO = db.drawingDAO();

        back_arrow = findViewById(R.id.back_arrow);
        text_tile = findViewById(R.id.text_title);
        title_text = findViewById(R.id.title_text);
        subtitle_text = findViewById(R.id.subtitle_text);
        date_text = findViewById(R.id.created_date_text);
        size_text = findViewById(R.id.size_text);
        container_drawing = findViewById(R.id.container_drawing);
        button_join = findViewById(R.id.button_join);

        intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            currentDrawing = drawingDAO.getDrawingByID(intent.getLongExtra("DrawingID", -1));
            drawingUser = userDAO.getUserByID(intent.getLongExtra("UserID", -1));
            isProvideDiscoverActivity = intent.getBooleanExtra("isDiscoverActivity", false);

            UpdateInformation();
        }

        back_arrow.setOnClickListener(v -> {
            finish();
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            // Go back to the Discover Activity
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

        button_join.setOnClickListener(v -> {
            intent = new Intent(context, DrawingActivity.class);
            intent.putExtra("DrawingID", currentDrawing.getId());
            startActivity(intent);
            finish();
        });
    }

    void UpdateInformation() {
        if(isProvideDiscoverActivity) button_join.setVisibility(View.GONE);
        else button_join.setVisibility(View.VISIBLE);
        String title = currentDrawing.getTitle();
        text_tile.setText(title);
        title_text.setText(title);

        String createdBy = getString(R.string.by) + " " + drawingUser.getUsername();
        subtitle_text.setText(createdBy);

        String size = currentDrawing.getWidth()+"x"+currentDrawing.getHeight();
        size_text.setText(size);

        Date drawingDate = currentDrawing.getCreationDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = getString(R.string.createdOn)+" "+dateFormat.format(drawingDate);
        date_text.setText(date);

        DrawingView drawingView = new DrawingView(context, null);
        drawingView.setSize(currentDrawing.getWidth(), currentDrawing.getHeight());
        drawingView.fromJSON(currentDrawing.getDrawingData());
        container_drawing.addView(drawingView.getDrawingPreview());

    }
}