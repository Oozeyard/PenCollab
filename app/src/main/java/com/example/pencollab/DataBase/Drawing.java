package com.example.pencollab.DataBase;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
                                parentColumns = "Uid",
                                childColumns = "OwnerId",
                                onDelete = 5)) // CASCADE
public class Drawing {
    @PrimaryKey(autoGenerate = true)
    public long Did;

    public long OwnerId;
    public String title;

}

