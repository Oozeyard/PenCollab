package com.example.pencollab.DataBase;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(tableName = "Drawings",
        indices = {@Index(value = "Did", unique = true)},
        foreignKeys = @ForeignKey(entity = User.class,
                                parentColumns = "Uid",
                                childColumns = "OwnerId",
                                onDelete = 5)) // CASCADE
public class Drawing {
    @PrimaryKey(autoGenerate = true)
    public long Did;

    public long OwnerId;
    public String title;
    public String drawingData; // Data (JSON)

    public Drawing() {}

    public Drawing(long ownerID, String title) {
        this.OwnerId = ownerID;
        this.title = title;
    }

    public long getId() { return Did; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public void setDrawingData(String drawingData) { this.drawingData = drawingData; }
    public String getDrawingData() { return drawingData; }
}

