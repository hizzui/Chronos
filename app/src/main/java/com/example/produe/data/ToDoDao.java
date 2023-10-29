package com.example.produe.data;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// DAO -> Data Access Object
@Dao
public interface ToDoDao {

    @Insert
    void insert(ToDoEntity toDoEntity);

    @Query("DELETE FROM todo_table")
    void deleteAll();

    @Query("SELECT * FROM todo_table WHERE is_done = 0 AND task_title IS NOT NULL ORDER BY deadline_date_year, deadline_date_month, deadline_date_day, deadline_time_hour, deadline_time_minute")
    LiveData<List<ToDoEntity>> getOrderedTasks();

    @Query("SELECT * FROM todo_table WHERE is_done = 0 AND task_title IS NOT NULL ORDER BY deadline_date_year, deadline_date_month, deadline_date_day, deadline_time_hour, deadline_time_minute")
    Cursor getOrderedTasksCursor();

    @Query("SELECT * FROM todo_table WHERE is_done = 1 AND task_title IS NOT NULL ORDER BY deadline_date_year DESC, deadline_date_month DESC, deadline_date_day DESC, deadline_time_hour DESC, deadline_time_minute DESC")
    LiveData<List<ToDoEntity>> getFinishedTasks();

    @Query("UPDATE todo_table SET is_done = :isTrue WHERE id = :id")
    void setIsDone(int id, Boolean isTrue);

    @Query("UPDATE todo_table SET task_title = :taskTitle, category = :category, deadline_date_year = :deadlineDateYear, deadline_date_month = :deadlineDateMonth, deadline_date_day = :deadlineDateDay, deadline_time_hour = :deadlineTimeHour, deadline_time_minute = :deadlineTimeMinute WHERE id = :id")
    void updateTask(int id, String taskTitle, String category, int deadlineDateYear, int deadlineDateMonth, int deadlineDateDay, int deadlineTimeHour, int deadlineTimeMinute);

    @Query("DELETE FROM todo_table WHERE id = :id")
    void deleteTask(int id);

    @Query("UPDATE todo_table SET category = :newCategory WHERE category = :oldCategory")
    void updateCategory(String oldCategory, String newCategory);

    @Query("SELECT id FROM todo_table WHERE category = :category")
    int[] getIDfromCategory(String category);

    @Query("UPDATE todo_table SET category = :newCategory WHERE id = :id")
    void updateCategoryFromID(String newCategory, int id);

    @Query("SELECT COUNT(*) FROM todo_table WHERE is_done = 1 AND category = :category")
    int countFinishedTasks(String category);

    @Query("SELECT COUNT(*) FROM todo_table WHERE is_done = 1")
    LiveData<Integer> countFinishedTasks();


    @Insert
    void insert(TimerEntity timerEntity);

    @Query("SELECT * FROM timer_table WHERE category <> 'None' ORDER BY time_elapsed_hours DESC, time_elapsed_minutes DESC, time_elapsed_seconds DESC")
    LiveData<List<TimerEntity>> getCategories();

    @Query("UPDATE timer_table SET time_elapsed_hours = :usedHours, time_elapsed_minutes = :usedMinutes, time_elapsed_seconds = :usedSeconds WHERE category = :category")
    void setUsedTime(String category, int usedHours, int usedMinutes, int usedSeconds);

    @Query("UPDATE timer_table SET category = :newCategory, color = :newColor WHERE category = :oldCategory")
    void updateTimer(String oldCategory, String newCategory, int newColor);

    @Query("DELETE FROM timer_table WHERE category = :category")
    void deleteTimer(String category);
}
