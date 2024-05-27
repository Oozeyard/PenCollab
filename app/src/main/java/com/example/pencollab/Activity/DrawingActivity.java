package com.example.pencollab.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.ColorPickerDialog;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.DrawingView;
import com.example.pencollab.R;

import java.util.Objects;

public class DrawingActivity extends AppCompatActivity {

    int width, height;
    ImageView back_arrow;
    EditText drawing_title;
    DrawingView drawing_view;
    LinearLayout container_buttons, container_chip_brush, container_chip_colors, container_eraser;
    Button save_button, share_button;
    Context context;
    User currentUser;
    DrawingDAO drawingDAO;

    Drawing currentDrawing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.drawing_activity);

        back_arrow = findViewById(R.id.back_arrow);
        drawing_view = findViewById(R.id.drawinView);
        drawing_title = findViewById(R.id.drawing_title);
        container_buttons = findViewById(R.id.container_button_share_and_save);
        container_chip_brush = findViewById(R.id.container_chip_brush);
        container_chip_colors = findViewById(R.id.container_chip_colors);
        container_eraser = findViewById(R.id.container_eraser);
        save_button = findViewById(R.id.button_save);
        share_button = findViewById(R.id.button_share);

        context = getApplicationContext();


        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(context);

        // Get DAO
        UserDAO userDAO = db.userDAO();
        drawingDAO = db.drawingDAO();

        // Get current user
        currentUser = userDAO.getCurrentUser();

        // Get the intent
        Intent intent = getIntent();

        // Use ViewTreeObserver to get the dimensions of drawing_view
        drawing_view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener to avoid multiple calls
                drawing_view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Get the dimensions of drawing_view
                width = drawing_view.getWidth();
                height = drawing_view.getHeight();
            }
        });


        // Get the drawing
        if (intent != null && intent.getExtras() != null) {
            long drawingID = intent.getLongExtra("DrawingID", 0);
            currentDrawing = drawingDAO.getDrawingByID(drawingID);
            drawing_title.setText(currentDrawing.getTitle());
            drawing_view.fromJSON(currentDrawing.getDrawingData());
        } else {
            currentDrawing = null;
        }

        // if user is not registered
        if (currentUser.getId() <= 1)  {
            share_button.setVisibility(View.INVISIBLE);
        }
        else container_buttons.setVisibility(View.VISIBLE);

        // Listener
        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            // Go back to the Main activity
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

        drawing_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Update Drawing's title
                String title = s.toString();
                if (currentDrawing == null) {
                    currentDrawing = new Drawing(currentUser.getId());
                    currentDrawing.width = width;
                    currentDrawing.height = height;
                    currentDrawing.Did = drawingDAO.insertDrawing(currentDrawing);
                }
                currentDrawing.setTitle(title);
                drawingDAO.updateDrawing(currentDrawing);
            }
        });

        // Brush
        container_chip_brush.setOnClickListener(v -> {
            drawing_view.setupPaint();
        });
        // Color palette
        container_chip_colors.setOnClickListener(colorPickerClickListener);
        // Eraser
        container_eraser.setOnClickListener(v -> {
            drawing_view.Eraser();
        });

        // Save & Share button
        save_button.setOnClickListener(v -> {
            if (currentDrawing != null) {
                currentDrawing.setDrawingData(drawing_view.toJSON());
                Toast.makeText(context, R.string.exporting, Toast.LENGTH_LONG).show();
                drawingDAO.updateDrawing(currentDrawing);
                if (drawing_view.Export(drawing_view.getContext(), currentDrawing.title)) {
                    Toast.makeText(context, R.string.exportTrue, Toast.LENGTH_LONG).show();
                } else  Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(context, R.string.noDrawing, Toast.LENGTH_LONG).show();
        });

    }

    public void updateDrawing(String DrawingData) {
        if (currentDrawing == null) {
            currentDrawing = new Drawing(currentUser.getId());
            currentDrawing.width = width;
            currentDrawing.height = height;
            currentDrawing.Did = drawingDAO.insertDrawing(currentDrawing);
        }
        currentDrawing.setDrawingData(DrawingData);
        drawingDAO.updateDrawing(currentDrawing);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    View.OnClickListener colorPickerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new ColorPickerDialog.Builder(v.getContext())
                    .setTitle(R.string.color_ask)
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton(R.string.select, (ColorEnvelopeListener) (envelope, fromUser) -> {
                        // get selected color
                        int color = envelope.getColor();
                        // use the color
                        drawing_view.setDrawingColor(color);
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                    .attachAlphaSlideBar(true) // Opacity
                    .attachBrightnessSlideBar(true) // Luminosity
                    .show();
        }
    };
}