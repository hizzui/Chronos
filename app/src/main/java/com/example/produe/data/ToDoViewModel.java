package com.example.produe.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ToDoViewModel extends AndroidViewModel {

    private ToDoRoomDatabase toDoRoomDatabase;
    private ToDoDao toDoDao;
    private LiveData<List<ToDoEntity>> mAllTasks;
    private LiveData<List<ToDoEntity>> mAllFinishedTasks;
    private LiveData<List<TimerEntity>> mAllCategories;
    private ToDoRepository mRepository;

    // Constructor
    public ToDoViewModel(Application application) {
        super(application);

        mRepository = new ToDoRepository(application);
        toDoRoomDatabase = ToDoRoomDatabase.getDatabase(application);
        toDoDao = toDoRoomDatabase.toDoDao();
        mAllTasks = toDoDao.getOrderedTasks();
        mAllCategories = toDoDao.getCategories();
        mAllFinishedTasks = toDoDao.getFinishedTasks();
    }

    public LiveData<List<ToDoEntity>> getAllTasks() {
        return mAllTasks;
    }

    public LiveData<List<ToDoEntity>> getAllFinishedTasks() {
        return mAllFinishedTasks;
    }

    public LiveData<List<TimerEntity>> getCategories() {
        return mAllCategories;
    }

    public void insert(ToDoEntity toDoEntity) {
        new InsertAsyncTask(toDoDao).execute(toDoEntity);
    }

    private static class InsertAsyncTask extends AsyncTask<ToDoEntity, Void, Void> {

        ToDoDao mTodoDao;

        private InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Void doInBackground(ToDoEntity... toDoEntities) {
            mTodoDao.insert(toDoEntities[0]);
            return null;
        }
    }

    public void insert(TimerEntity toDoEntity) {
        new InsertAsyncTaskCategory(toDoDao).execute(toDoEntity);
    }

    private static class InsertAsyncTaskCategory extends AsyncTask<TimerEntity, Void, Void> {

        ToDoDao mTodoDao;

        private InsertAsyncTaskCategory(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Void doInBackground(TimerEntity... toDoEntities) {
            mTodoDao.insert(toDoEntities[0]);
            return null;
        }
    }

     public void setIsDone(int id, boolean isDone) {
        mRepository.setIsDone(id, isDone);
     }

     public void updateTask(int id, String taskTitle, String category, int deadlineDateYear, int deadlineDateMonth, int deadlineDateDay, int deadlineTimeHour, int deadlineTimeMinute) {
        mRepository.updateTask(id, taskTitle, category, deadlineDateYear, deadlineDateMonth, deadlineDateDay, deadlineTimeHour, deadlineTimeMinute);
     }

     public void deleteTask(int id) {
        mRepository.deleteTask(id);
     }

    public void setTimeUsed(String category, int usedHours, int usedMinutes, int usedSeconds) {
        mRepository.setTimeUsed(category, usedHours, usedMinutes, usedSeconds);
    }

    public void updateTimer(String oldCategory, String newCategory, int newColor) {
        mRepository.updateTimer(oldCategory, newCategory, newColor);
    }

    public void deleteTimer(String category) {
        mRepository.deleteTimer(category);
    }

    public int getTaskCountCategory(String category) {
        return mRepository.getTaskCount(category);
    }

}
