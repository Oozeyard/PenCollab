package com.example.pencollab.DataBase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pencollab.DataBase.Drawing;

import java.util.List;

@Dao
public interface DrawingDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertDrawing(Drawing drawing); // Return Id
    @Update
    void updateDrawing(Drawing... drawings);
    @Delete
    void deleteDrawing(Drawing drawing);

    @Query("SELECT * FROM Drawings WHERE Did = :Did")
    Drawing getDrawingByID(long Did);
    @Query("SELECT * FROM Drawings WHERE Did IN (:Did)")
    List<Drawing> getDrawingsByIDs(List<Long> Did);
    @Query("SELECT * FROM Drawings WHERE isPublic = true")
    List<Drawing> getPublicDrawings();
    @Query("SELECT * FROM Drawings WHERE isPublic = true AND title = :title")
    List<Drawing> getPublicDrawingsByName(String title);
    @Query("SELECT * FROM Drawings WHERE isPublic = 1 AND title LIKE '%' || :searchString || '%'")
    List<Drawing> getPublicDrawingsByString(String searchString);
    @Query("SELECT * FROM Drawings WHERE ownerId = :ownerId")
    List<Drawing> getDrawingsByOwnerID(long ownerId);
    @Query("SELECT OwnerId FROM Drawings WHERE Did = :Did")
    long getOwnerId(long Did);

    @Query("SELECT * FROM drawings")
    List<Drawing> getAllDrawings();

}
