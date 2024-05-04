package com.example.pencollab.DataBase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pencollab.DataBase.Drawing;
import com.example.pencollab.DataBase.User;

@Dao
public interface DrawingDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertDrawing(Drawing user); // Return UserID
    @Update
    void updateDrawing(Drawing... drawings);
    @Delete
    void deleteDrawing(Drawing drawing);

    @Query("SELECT * FROM Drawing WHERE Did = :Did")
    Drawing getDrawingByID(int Did);
    @Query("SELECT OwnerId FROM Drawing WHERE Did = :Did")
    long getOwnerId(int Did);

}
