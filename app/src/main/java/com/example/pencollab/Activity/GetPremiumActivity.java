package com.example.pencollab.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pencollab.DataBase.AppDatabase;
import com.example.pencollab.DataBase.DAO.UserDAO;
import com.example.pencollab.DataBase.DatabaseHolder;
import com.example.pencollab.DataBase.User;
import com.example.pencollab.R;

public class GetPremiumActivity extends AppCompatActivity {

    ImageView back_arrow;
    Button buy_button;
    Context context;
    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.get_premium);

        context = getApplicationContext();
        AppDatabase db = DatabaseHolder.getInstance(context);
        UserDAO userDAO = db.userDAO();

        currentUser = userDAO.getCurrentUser();

        back_arrow = findViewById(R.id.back_arrow);
        buy_button = findViewById(R.id.button_buy);

        back_arrow.setOnClickListener(v -> {
            this.startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        buy_button.setOnClickListener(v -> {
            if (currentUser.getId() <= 1) {
                Toast.makeText(context, R.string.unregistered, Toast.LENGTH_LONG).show();
                return;
            }
            else if (currentUser.isPremium) {
                Toast.makeText(context, R.string.ispremium, Toast.LENGTH_LONG).show();
                return;
            }
            else {
                currentUser.isPremium = true;
                userDAO.updateUser(currentUser);
                Toast.makeText(context, R.string.nowpremium, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}