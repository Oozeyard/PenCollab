package com.example.pencollab.DataBase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.pencollab.DrawingView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


@Entity(tableName = "Drawings",
        foreignKeys = @ForeignKey(entity = User.class,
                                parentColumns = "Uid",
                                childColumns = "OwnerId",
                                onDelete = 5)) // CASCADE
public class Drawing {
    @PrimaryKey(autoGenerate = true)
    public long Did;
    @ColumnInfo(index = true)
    public long OwnerId;
    public String title;
    public String drawingData; // Data (JSON)
    public int width, height;
    public boolean isPublic;
    public Date creationDate;

    public Drawing() {}

    public Drawing(long ownerID) {
        this.OwnerId = ownerID;
        this.isPublic = false;
        this.creationDate = new Date();
    }

    public long getId() { return Did; }

    public long getOwnerId() { return OwnerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public void setDrawingData(String drawingData) { this.drawingData = drawingData; }
    public String getDrawingData() { return drawingData; }

    public void setVisibility(boolean visibility) { this.isPublic = visibility; }
    public boolean getVisibility() { return isPublic; }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public Date getCreationDate() { return creationDate; }
}

