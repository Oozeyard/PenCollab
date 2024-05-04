package com.example.pencollab.DataBase.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.pencollab.DataBase.User;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertUser(User user); // Return UserID
    @Update
    void updateUser(User... users);
    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM Users WHERE Uid = :Uid")
    User getUserByID(int Uid);
    @Query("SELECT isPremium FROM Users WHERE Uid = :Uid")
    boolean getPremiumByID(int Uid);

    @Query("DELETE FROM Users")
    void nukeTable(); // o7
}
