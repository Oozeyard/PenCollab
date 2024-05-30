package com.example.pencollab.DataBase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.pencollab.DataBase.History;

import java.util.List;

@Dao
public interface HistoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHistory(History history);
    @Delete
    void deleteHistory(History history);

    @Query("SELECT * FROM History WHERE Uid = :Uid AND Did = :Did")
    List<History> getHistoryByDidUid(long Uid, long Did);

    @Query("DELETE FROM History WHERE Did = :Did")
    void deleteHistoriesById(long Did);
}
