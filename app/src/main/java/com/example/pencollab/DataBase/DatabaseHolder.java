package com.example.pencollab.DataBase;

import android.content.Context;

import androidx.room.Room;

import com.example.pencollab.DataBase.AppDatabase;

public class DatabaseHolder {
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            // Build Database
            // WARNING : ENLEVER .allowMainThreadQueries() ET METTRE UN THREAD A PART
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "database-name").allowMainThreadQueries().build();
            // WARNING : ENLEVER .allowMainThreadQueries() ET METTRE UN THREAD A PART
        }
        return instance;
    }
}

