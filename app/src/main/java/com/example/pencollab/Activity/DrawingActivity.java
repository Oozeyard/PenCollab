package com.example.pencollab.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

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

public class DrawingActivity extends AppCompatActivity {

    int width, height, color;
    ImageView back_arrow;
    EditText drawing_title;
    DrawingView drawing_view;
    LinearLayout container_buttons, container_chip_brush, container_chip_colors, container_stroke_width, container_eraser;
    private SeekBar brushSizeSlider;
    private View brushSizeIndicator;
    Drawing currentDrawing;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.drawing_activity);

        context = this;

        back_arrow = findViewById(R.id.back_arrow);
        drawing_view = findViewById(R.id.drawinView);
        drawing_title = findViewById(R.id.drawing_title);
        container_buttons = findViewById(R.id.container_button_share_and_save);
        container_chip_brush = findViewById(R.id.container_chip_brush);
        container_chip_colors = findViewById(R.id.container_chip_colors);
        container_stroke_width = findViewById(R.id.container_stroke_width);
        container_eraser = findViewById(R.id.container_eraser);

        brushSizeSlider = findViewById(R.id.brush_size_slider);
        brushSizeIndicator = findViewById(R.id.brush_size_indicator);

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(getApplicationContext());

        // Get DAO
        UserDAO userDAO = db.userDAO();
        DrawingDAO drawingDAO = db.drawingDAO();

        // Get current user
        User currentUser = userDAO.getCurrentUser();

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
        if (currentUser.getId() <= 1) container_buttons.setVisibility(View.INVISIBLE);
        else container_buttons.setVisibility(View.VISIBLE);

        back_arrow.setOnClickListener(v -> {
            String title = drawing_title.getText().toString();

            if (currentDrawing == null) {
                Drawing newDrawing = new Drawing(currentUser.getId(), title);
                newDrawing.setDrawingData(drawing_view.toJSON());
                newDrawing.width = width;
                newDrawing.height = height;
                drawingDAO.insertDrawing(newDrawing);
            } else {
                currentDrawing.setTitle(title);
                currentDrawing.setDrawingData(drawing_view.toJSON());
                drawingDAO.updateDrawing(currentDrawing);
            }

            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        container_chip_brush.setOnClickListener(v -> {
            drawing_view.initializePen();
        });

        container_chip_colors.setOnClickListener(colorPickerClickListener);

        container_eraser.setOnClickListener(v -> {
            Toast.makeText(this, "Erases", Toast.LENGTH_SHORT).show();
            drawing_view.initializeEraser();
        });

        container_stroke_width.setOnClickListener(v -> {
            if (brushSizeSlider.getVisibility() == View.GONE) {
                int Size = (int) drawing_view.getPenSize();
                brushSizeIndicator.getLayoutParams().width = Size;
                brushSizeIndicator.getLayoutParams().height = Size;
                brushSizeSlider.setProgress(Size);
                brushSizeSlider.setVisibility(View.VISIBLE);
                brushSizeIndicator.setVisibility(View.VISIBLE);
            } else {
                brushSizeSlider.setVisibility(View.GONE);
                brushSizeIndicator.setVisibility(View.GONE);
            }
        });

        brushSizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float brushSize = progress + 1; // To avoid a size of 0
                drawing_view.setPenSize(brushSize);
                updateBrushSizeIndicator(brushSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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
                        color = envelope.getColor();
                        // use the color
                        drawing_view.setDrawingColor(color);

                        GradientDrawable background = (GradientDrawable) brushSizeIndicator.getBackground();
                        background.setColor(color);
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                    .attachAlphaSlideBar(true) // Opacity
                    .attachBrightnessSlideBar(true) // Luminosity
                    .show();
        }
    };

    private void updateBrushSizeIndicator(float size) {
        ViewGroup.LayoutParams params = brushSizeIndicator.getLayoutParams();
        params.width = (int) size;
        params.height = (int) size;
        brushSizeIndicator.setLayoutParams(params);
    }
}