package com.example.pencollab.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;

// Init Database
@Database(entities = {User.class, Drawing.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract DrawingDAO drawingDAO();
}
