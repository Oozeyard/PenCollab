package com.example.pencollab.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.R;

public class LoginActivity extends AppCompatActivity {

    ImageView back_arrow;
    EditText username, email, password;
    Button forgot, login, signup;
    String usernameS, emailS, passwordS;

    UserDAO userDAO;
    DrawingDAO drawingDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        back_arrow = findViewById(R.id.back_arrow);
        username = findViewById(R.id.editText_enter_username);
        email = findViewById(R.id.editText_enter_email);
        password = findViewById(R.id.editText_enter_password);
        forgot = findViewById(R.id.button_forgot_password);
        login = findViewById(R.id.button_login);
        signup = findViewById(R.id.button_sign_up);

        // Get Database
        AppDatabase db = DatabaseHolder.getInstance(getApplicationContext());

        // Get DAO
        userDAO = db.userDAO();
        drawingDAO = db.drawingDAO();

        // Get current user
        User currentUser = userDAO.getCurrentUser();

        signup.setOnClickListener(v -> {
            if (getStringTxt()) return;

            User user = userDAO.getUserByEmail(emailS);

            if (user != null) {
                showAlert(getString(R.string.user_already_exist));
                return;
            }

            user = new User(usernameS, emailS, passwordS, false);

            // set the new currentUser
            currentUser.setCurrentUser(false);
            userDAO.updateUser(currentUser);

            user.setCurrentUser(true);
            userDAO.insertUser(user);

            nice();
        });

        login.setOnClickListener(v -> {
            if (getStringTxt()) return;

            User user = userDAO.getUserByEmail(emailS);

            if (user == null) {
                showAlert(getString(R.string.user_doesnt_exist));
                return;
            }

            if (!usernameS.equals(user.getUsername())) {
                showAlert(getString(R.string.wrong_username));
                return;
            }

            if (!passwordS.equals(user.getPassword())) {
                showAlert(getString(R.string.wrong_password));
                return;
            }

            currentUser.setCurrentUser(false);
            userDAO.updateUser(currentUser);

            user.setCurrentUser(true);
            userDAO.updateUser(user);

            nice();
        });

        forgot.setOnClickListener(v -> {

        });

        back_arrow.setOnClickListener(v -> {
            nice();
        });
    }

    private void nice() {
        this.startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean getStringTxt() {
        usernameS = username.getText().toString().trim();
        emailS = email.getText().toString().trim();
        passwordS = password.getText().toString().trim();

        if (usernameS.isEmpty() || emailS.isEmpty() || passwordS.isEmpty()) {
            showAlert(getString(R.string.empty_fields));
            return true;
        }else return false;
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("")
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}