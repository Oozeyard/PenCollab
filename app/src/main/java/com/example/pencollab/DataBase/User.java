package com.example.pencollab.DataBase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Users",
        indices = {@Index(value = "email", unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long Uid;

    public String email;

    @NonNull
    public String username, password;
    boolean isPremium;

    @Ignore
    public User() {
        this.username = "User";
        this.isPremium = false;
        this.email = "none";
        this.password = "none";
    }

    public User(String username, String password, String email, Boolean premium) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isPremium = premium;
    }

    public long getId() {
        return Uid;
    }

    public String getUsername() {
        return username;
    }

    public boolean getPremium() {
        return isPremium;
    }

}
