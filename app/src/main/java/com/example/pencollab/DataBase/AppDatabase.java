package com.example.pencollab.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.pencollab.DataBase.DAO.DrawingDAO;
import com.example.pencollab.DataBase.DAO.DrawingUserDAO;
import com.example.pencollab.DataBase.DAO.HistoryDAO;
import com.example.pencollab.DataBase.DAO.UserDAO;

// Init Database
@Database(entities = {User.class, Drawing.class, DrawingUser.class, History.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract DrawingDAO drawingDAO();
    public abstract DrawingUserDAO drawingUserDAO();
    public abstract HistoryDAO historyDAO();
}
