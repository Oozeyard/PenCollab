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

    @NonNull
    public String username, email, password;
    public boolean isPremium, isCurrentUser;

    public User() {
        this.username = "User";
        this.email = "none";
        this.password = "none";
        this.isPremium = false;
        this.isCurrentUser = false;
    }

    public User(@NonNull String username, @NonNull String email, @NonNull String password, Boolean premium) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isPremium = premium;
        this.isCurrentUser = false;
    }

    public long getId() { return Uid; }

    @NonNull
    public String getUsername() { return username; }
    public void setUsername(@NonNull String username) { this.username = username; }

    @NonNull
    public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    @NonNull
    public String getPassword() { return password; }
    public void setPassword(@NonNull String password) { this.password = password; }

    public boolean getPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }

    public void setCurrentUser(boolean currentUser) { isCurrentUser = currentUser; }
}
