package com.example.pencollab.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.pencollab.DataBase.DAO.DrawingUserDAO;
import com.example.pencollab.DataBase.DrawingUser;
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
    SeekBar brushSizeSlider;
    View brushSizeIndicator;
    Button save_button, share_button;
    Context context;
    User currentUser;
    DrawingDAO drawingDAO;
    UserDAO userDAO;
    DrawingUserDAO drawingUserDAO;

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
        save_button = findViewById(R.id.button_save);
        share_button = findViewById(R.id.button_share);

        context = getApplicationContext();

        brushSizeSlider = findViewById(R.id.brush_size_slider);
        brushSizeIndicator = findViewById(R.id.brush_size_indicator);

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(context);

        // Get DAO
        userDAO = db.userDAO();
        drawingDAO = db.drawingDAO();
        drawingUserDAO = db.drawingUserDAO();

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
            drawing_view.initializePen();
        });
        // Color palette
        container_chip_colors.setOnClickListener(colorPickerClickListener);
        // Eraser
        container_eraser.setOnClickListener(v -> {
            Toast.makeText(this, "Erases", Toast.LENGTH_SHORT).show();
            drawing_view.initializeEraser();
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

        share_button.setOnClickListener(v -> {
            share_drawing();
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

    // Share button UI
    public void share_drawing() {
        final EditText input = new EditText(this);
        final CheckBox visibilityCheckbox = new CheckBox(this);
        visibilityCheckbox.setText(R.string.Visibility);
        visibilityCheckbox.setChecked(currentDrawing.getVisibility());

        // Layout for both sharing
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(visibilityCheckbox);

        // AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.share);
        builder.setMessage(R.string.username);
        builder.setView(layout);

        // button
        builder.setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = input.getText().toString();

                if (!username.isEmpty()){
                    User invitedUser = userDAO.getUserByUsername(username);
                    // Check if the user exist and isn't himself
                    if (invitedUser == null || invitedUser.getId() == 1 || invitedUser.username.equals(currentUser.username))
                        Toast.makeText(context, R.string.user_doesnt_exist, Toast.LENGTH_LONG).show();
                    else {
                        // Add to db
                        DrawingUser sharedDrawing = new DrawingUser(currentDrawing.getId(), invitedUser.getId());
                        drawingUserDAO.insertCross(sharedDrawing);
                        String message = R.string.shared_with + invitedUser.getUsername();
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                }
                else Toast.makeText(context, R.string.enter_other_username, Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        visibilityCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentDrawing.setVisibility(isChecked);
                drawingDAO.updateDrawing(currentDrawing);
            }
        });

        // show
        builder.show();
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