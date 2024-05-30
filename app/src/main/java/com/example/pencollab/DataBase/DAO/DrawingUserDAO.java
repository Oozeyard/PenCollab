package com.example.pencollab.DataBase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pencollab.DataBase.DrawingUser;

import java.util.List;

@Dao
public interface DrawingUserDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertCross(DrawingUser crossRef);
    @Update
    void updateCross(DrawingUser... drawingUsers);
    @Delete
    void deleteCross(DrawingUser drawingUser);

    @Query("SELECT UserId FROM DrawingUser WHERE Did = :Did")
    List<Long> getSharedUserID(long Did);

    @Query("SELECT Did FROM DrawingUser WHERE UserId = :Uid")
    List<Long> getSharedDrawingID(long Uid);

    @Query("SELECT Drawings.Did FROM Drawings " +
            "INNER JOIN DrawingUser ON Drawings.Did = DrawingUser.Did " +
            "WHERE DrawingUser.UserId = :Uid AND Drawings.title LIKE '%' || :searchString || '%'")
    List<Long> getSharedDrawingIDsByTitle(long Uid, String searchString);
}
