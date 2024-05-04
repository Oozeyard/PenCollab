package com.example.pencollab.DataBase;

import androidx.room.Entity;

@Entity(primaryKeys = {"Did", "UserId"})
public class DrawingUserCrossRef { // User who've got access to the drawing
    public long Did;
    public long UserId;
}
