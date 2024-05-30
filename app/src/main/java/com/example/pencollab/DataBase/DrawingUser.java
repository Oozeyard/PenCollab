package com.example.pencollab.DataBase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "DrawingUser", foreignKeys = {
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
public class DrawingUser { // User who've got access to the drawing
    @PrimaryKey(autoGenerate = true)
    public long CrossId;
    @ColumnInfo(index = true)
    public final long Did;
    @ColumnInfo(index = true)
    public final long UserId;

    public DrawingUser(long Did, long UserId) {
        this.Did = Did; // Drawing to share
        this.UserId = UserId; // User shared with
    }
}
