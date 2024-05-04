package com.example.pencollab.DataBase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pencollab.DataBase.DrawingUserCrossRef;

import java.util.List;

@Dao
public interface DrawingUserCrossRefDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long[] insertCross(DrawingUserCrossRef crossRef);
    @Delete
    void deleteCross();

    @Query("SELECT UserId FROM DrawingUserCrossRef WHERE Did = :Did")
    List<Long> getSharedID(int Did);
}
