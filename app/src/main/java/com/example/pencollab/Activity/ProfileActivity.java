package com.example.pencollab.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.DiscoverArrayAdapter;
import com.example.pencollab.R;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    ImageView back_arrow;

    TextView user_textView, status_textView;
    LinearLayout button_log_out;
    RecyclerView drawing_list;
    Button search_button;
    DrawingDAO drawingDAO;
    UserDAO userDAO;

    User currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_activity);

        back_arrow = findViewById(R.id.back_arrow);
        user_textView = findViewById(R.id.txt_user_name);
        status_textView = findViewById(R.id.txt_user_status);
        button_log_out = findViewById(R.id.container_button_log_out);
        drawing_list = findViewById(R.id.drawing_list);
        search_button = findViewById(R.id.search_button);

        drawing_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(getApplicationContext());

        // Get DAO
        userDAO = db.userDAO();
        drawingDAO = db.drawingDAO();

        // Get the intent
        Intent intent = getIntent();
        // Get user
        if (intent != null && intent.getExtras() != null) {
            long userID = intent.getLongExtra("UserID", -1);
            currentUser = userDAO.getUserByID(userID);
        } else {
            currentUser = userDAO.getCurrentUser(); // In case of intent error
        }

        user_textView.setText(currentUser.getUsername());

        if (currentUser.getPremium()) status_textView.setText(R.string.premium);
        else status_textView.setText(R.string.not_premium);

        if (!currentUser.isCurrentUser) button_log_out.setVisibility(View.GONE);

        button_log_out.setOnClickListener(v -> {
            currentUser.setCurrentUser(false);
            userDAO.updateUser(currentUser);

            currentUser = userDAO.getUserByID(1); // default user
            currentUser.setCurrentUser(true);
            userDAO.updateUser(currentUser);

            nice();
        });

        back_arrow.setOnClickListener(v -> nice());

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            // Go back to the Main activity
            @Override
            public void handleOnBackPressed() {
                nice();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

        // Drawing list
        ArrayList<Drawing> publicDrawings = new ArrayList<>(drawingDAO.getPublicDrawingsByUserID(currentUser.getId()));
        drawing_list.setAdapter(new DiscoverArrayAdapter(this, publicDrawings));

        // Search Bar
        search_button.setOnClickListener(v -> searchBuilder());

    }

    private void nice() {
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void searchBuilder() {
        // Create an EditText to be used in the AlertDialog
        final EditText usernameInput = new EditText(this);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_other_username);
        builder.setView(usernameInput);

        // Set up the buttons
        builder.setPositiveButton(R.string.search, (dialog, which) -> {
            // Get user
            String username = usernameInput.getText().toString();
            User search_user = userDAO.getUserByUsername(username);
            if (search_user == null ||search_user.getId() <= 1) Toast.makeText(ProfileActivity.this, R.string.user_doesnt_exist, Toast.LENGTH_LONG).show();
            else {
                // Start a new activity and pass the username
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                intent.putExtra("UserID", search_user.getId());
                startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
}