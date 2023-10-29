package com.example.produe.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timer_table")
public class TimerEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "category")
    public String mCategory;

    @ColumnInfo(name = "color")
    public int mColor;

    @ColumnInfo(name = "time_elapsed_seconds")
    public int mTimeElapsedSeconds;

    @ColumnInfo(name = "time_elapsed_minutes")
    public int mTimeElapsedMinutes;

    @ColumnInfo(name = "time_elapsed_hours")
    public int mTimeElapsedHours;

    public TimerEntity(@NonNull String category, int color, int timeElapsedSeconds, int timeElapsedMinutes, int timeElapsedHours) {
        this.mCategory = category;
        this.mColor = color;
        this.mTimeElapsedSeconds = timeElapsedSeconds;
        this.mTimeElapsedMinutes = timeElapsedMinutes;
        this.mTimeElapsedHours = timeElapsedHours;
    }
}
