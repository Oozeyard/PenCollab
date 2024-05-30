package com.example.pencollab.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "History", foreignKeys = {
        @ForeignKey(
                entity = Drawing.class,
                parentColumns = "Did",
                childColumns = "Did",
                onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
                entity = User.class,
                parentColumns = "Uid",
                childColumns = "Uid",
                onDelete = ForeignKey.CASCADE
        )
})
public class History {

    @PrimaryKey(autoGenerate = true)
    public long Hid; // History ID

    // Foreign key -> DrawingID & UserID
    @ColumnInfo(index = true)
    public final long Did;
    @ColumnInfo(index = true)
    public final long Uid;

    public final String drawingData;
    public Date creationDate;
    public final int width;
    public final int height;

    public History(long Uid, long Did, String drawingData, int width, int height) {
        this.Did = Did;
        this.Uid = Uid;
        this.width = width;
        this.height = height;
        this.drawingData = drawingData;
        this.creationDate = new Date();
    }

    public String getDrawingData() { return drawingData; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Date getCreationDate() { return creationDate; }
}
