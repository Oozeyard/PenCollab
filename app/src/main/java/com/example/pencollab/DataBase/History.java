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
                childColumns = "UserId",
                onDelete = ForeignKey.CASCADE
        )
})
public class History {

    @PrimaryKey(autoGenerate = true)
    public long Hid; // History ID

    // Foreign key -> DrawingID & UserID
    @ColumnInfo(index = true)
    public long Did;
    @ColumnInfo(index = true)
    public long Uid;

    public String drawingData;
    public Date creationDate;

    public History(long userID, long drawingID, String drawingData) {
        this.Did = drawingID;
        this.Uid = userID;
        this.drawingData = drawingData;
        this.creationDate = new Date();
    }

    public String getDrawingData() { return drawingData; }
}
