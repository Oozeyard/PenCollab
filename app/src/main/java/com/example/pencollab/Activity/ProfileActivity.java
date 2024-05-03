package com.example.pencollab.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pencollab.R;

public class ProfileActivity extends AppCompatActivity {

    ImageView back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_activity);

        back_arrow = findViewById(R.id.back_arrow);

        back_arrow.setOnClickListener(v -> this.startActivity(new Intent(this, MainActivity.class)));
    }
}