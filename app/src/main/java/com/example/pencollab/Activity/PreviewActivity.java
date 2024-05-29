package com.example.pencollab.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            currentDrawing = drawingDAO.getDrawingByID(intent.getLongExtra("DrawingID", -1));
            drawingUser = userDAO.getUserByID(intent.getLongExtra("UserID", -1));
            isProvideDiscoverActivity = intent.getBooleanExtra("isDicoverActivity", false);

            UpdateInformation();
        }

        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            // Go back to the Main activity
            @Override
            public void handleOnBackPressed() {
                Intent intent;
                /*if (isProvideDiscoverActivity) intent = new Intent(context, DiscoverActivity.class);
                else*/ intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
    }

    void UpdateInformation() {
        String title = currentDrawing.getTitle();
        if (title.isEmpty()) title = "Unknow";
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