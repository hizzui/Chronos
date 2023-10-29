package com.example.produe.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo_table", foreignKeys = @ForeignKey(entity = TimerEntity.class, parentColumns = "category", childColumns = "category", onDelete = ForeignKey.NO_ACTION))
public class ToDoEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int mID;

    @ColumnInfo(name = "task_title")
    public String mTaskTitle;

    @ColumnInfo(name = "deadline_date_year")
    public int mDateYear;

    @ColumnInfo(name = "deadline_date_month")
    public int mDateMonth;

    @ColumnInfo(name = "deadline_date_day")
    public int mDateDay;

    @ColumnInfo(name = "deadline_time_hour")
    public int mTimeHour;

    @ColumnInfo(name = "deadline_time_minute")
    public int mTimeMinute;

    @ColumnInfo(name = "is_done")
    public boolean mIsDone;

    @NonNull
    @ColumnInfo(name = "category")
    public String mCategory;


    public ToDoEntity(String taskTitle, @NonNull String category) {
        this.mTaskTitle = taskTitle;
        this.mCategory = category;
        this.mDateYear = 9999;
        this.mDateMonth = 99;
        this.mDateDay = 99;
        this.mTimeHour = 99;
        this.mTimeMinute = 99;
        this.mIsDone = false;
    }

    public ToDoEntity(String taskTitle, @NonNull String category, int deadlineDateYear, int deadlineDateMonth, int deadlineDateDay) {
        this.mTaskTitle = taskTitle;
        this.mCategory = category;
        this.mDateYear = deadlineDateYear;
        this.mDateMonth = deadlineDateMonth;
        this.mDateDay = deadlineDateDay;
        this.mTimeHour = 99;
        this.mTimeMinute = 99;
        this.mIsDone = false;
    }

    public ToDoEntity(String taskTitle, @NonNull String category, int deadlineDateYear, int deadlineDateMonth, int deadlineDateDay, int deadlineTimeHour, int deadlineTimeMinute) {
        this.mTaskTitle = taskTitle;
        this.mCategory = category;
        this.mDateYear = deadlineDateYear;
        this.mDateMonth = deadlineDateMonth;
        this.mDateDay = deadlineDateDay;
        this.mTimeHour = deadlineTimeHour;
        this.mTimeMinute = deadlineTimeMinute;
        this.mIsDone = false;
    }

    public String getTaskTitle() {
        return this.mTaskTitle;
    }

    public int getId() {
        return this.mID;
    }
}
