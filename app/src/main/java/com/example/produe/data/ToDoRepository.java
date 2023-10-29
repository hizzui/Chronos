package com.example.produe.data;

import android.app.Application;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

public class ToDoRepository {
    private static ToDoDao mToDoDao;

    ToDoRepository(Application application) {
        ToDoRoomDatabase database = ToDoRoomDatabase.getDatabase(application);
        mToDoDao = database.toDoDao();
    }

    public void insert(ToDoEntity toDoEntity) {
        new ToDoRepository.InsertAsyncTask(mToDoDao).execute(toDoEntity);
    }

    private static class InsertAsyncTask extends AsyncTask<ToDoEntity, Void, Void> {

        private ToDoDao mTodoDao;

        private InsertAsyncTask(ToDoDao mTodoDao) {
            this.mTodoDao = mTodoDao;
        }

        @Override
        protected Void doInBackground(ToDoEntity... toDoEntities) {
            mTodoDao.insert(toDoEntities[0]);
            return null;
        }
    }

    void setIsDone(int id, boolean isDone) {
        new ToDoRepository.QueryAsyncTask(mToDoDao, id, isDone).execute("taskTitle");
    }

    private static class QueryAsyncTask extends AsyncTask<String, Void, List<ToDoEntity>> {

        private ToDoDao mTodoDao;
        private int mID;
        private boolean mIsDone;

        QueryAsyncTask(ToDoDao dao, int id, boolean isDone) {
            mTodoDao = dao;
            mID = id;
            mIsDone = isDone;
        }

        @Override
        protected List<ToDoEntity> doInBackground(String ... strings) {
            mTodoDao.setIsDone(mID, mIsDone);
            return null;
        }
    }

    void updateTask(int id, String taskTitle, String category, int deadlineDateYear, int deadlineDateMonth, int deadlineDateDay, int deadlineTimeHour, int deadlineTimeMinute) {
        new ToDoRepository.UpdateAsyncTask(mToDoDao, id, taskTitle, category, deadlineDateYear, deadlineDateMonth, deadlineDateDay, deadlineTimeHour, deadlineTimeMinute).execute("update");
    }

    private static class UpdateAsyncTask extends AsyncTask<String, Void, List<ToDoEntity>> {

        private ToDoDao mTodoDao;
        private int mID;
        private String mTaskTitle;
        private String mCategory;
        private int mDeadlineDateYear;
        private int mDeadlineDateMonth;
        private int mDeadlineDateDay;
        private int mDeadlineTimeHour;
        private int mDeadlineTimeMinute;

        UpdateAsyncTask(ToDoDao toDoDao, int id, String taskTitle, String category, int deadlineDateYear, int deadlineDateMonth, int deadlineDateDay, int deadlineTimeHour, int deadlineTimeMinute) {
            mTodoDao = toDoDao;
            mID = id;
            mTaskTitle = taskTitle;
            mCategory = category;
            mDeadlineDateYear = deadlineDateYear;
            mDeadlineDateMonth = deadlineDateMonth;
            mDeadlineDateDay = deadlineDateDay;
            mDeadlineTimeHour = deadlineTimeHour;
            mDeadlineTimeMinute = deadlineTimeMinute;
        }

        @Override
        protected List<ToDoEntity> doInBackground(String... strings) {
            mTodoDao.updateTask(mID, mTaskTitle, mCategory, mDeadlineDateYear, mDeadlineDateMonth, mDeadlineDateDay, mDeadlineTimeHour, mDeadlineTimeMinute);
            return null;
        }
    }

    void deleteTask(int id) {
        Log.i("selfmade", "inside delete in repository");
        new ToDoRepository.DeleteAsyncTask(mToDoDao, id).execute("delete");
    }

    private static class DeleteAsyncTask extends AsyncTask<String, Void, List<ToDoEntity>> {

        private ToDoDao mTodoDao;
        private int mID;

        DeleteAsyncTask(ToDoDao toDoDao, int id) {
            mTodoDao = toDoDao;
            mID = id;
        }

        @Override
        protected List<ToDoEntity> doInBackground(String... strings) {
            mTodoDao.deleteTask(mID);
            Log.i("selfmade", "inside delete in repository do in background");
            return null;
        }
    }

    void setTimeUsed(String category, int usedHours, int usedMinutes, int usedSeconds) {
        new ToDoRepository.QueryAsyncTaskTimeUsed(mToDoDao, usedHours, usedMinutes, usedSeconds).execute(category);
    }

    private static class QueryAsyncTaskTimeUsed extends AsyncTask<String, Void, List<ToDoEntity>> {

        private ToDoDao mTodoDao;
        private  int mUsedHours;
        private int mUsedMinutes;
        private int mUsedSeconds;

        QueryAsyncTaskTimeUsed(ToDoDao dao, int usedHours, int usedMinutes, int usedSeconds) {
            mTodoDao = dao;
            mUsedHours = usedHours;
            mUsedMinutes = usedMinutes;
            mUsedSeconds = usedSeconds;
        }

        @Override
        protected List<ToDoEntity> doInBackground(String ... strings) {
            mTodoDao.setUsedTime(strings[0], mUsedHours, mUsedMinutes, mUsedSeconds);
            return null;
        }
    }

    void updateTimer(String oldCategory, String newCategory, int newColor) {
        new ToDoRepository.UpdateAsyncTaskTimer(oldCategory, newCategory, newColor).execute("update");
    }

    public static class UpdateAsyncTaskTimer extends AsyncTask<String, Void, List<TimerEntity>> {
        String oldCategory;
        String newCategory;
        int newColor;

        UpdateAsyncTaskTimer(String oldCategory, String newCategory, int newColor) {
            this.oldCategory = oldCategory;
            this.newCategory = newCategory;
            this.newColor = newColor;
        }

        @Override
        protected List<TimerEntity> doInBackground(String... strings) {
            int [] ids = mToDoDao.getIDfromCategory(oldCategory);
            mToDoDao.updateCategory(oldCategory, "None");
            mToDoDao.updateTimer(oldCategory, newCategory, newColor);
            for (int id : ids) {
                mToDoDao.updateCategoryFromID(newCategory, id);
            }
            return null;
        }
    }

    void deleteTimer(String category) {
        new ToDoRepository.DeleteAsyncTaskTimer(category).execute("delete");
    }

    public static class DeleteAsyncTaskTimer extends AsyncTask<String, Void, List<TimerEntity>> {
        String category;

        DeleteAsyncTaskTimer(String category) {
            this.category = category;
        }

        @Override
        protected List<TimerEntity> doInBackground(String... strings) {
            mToDoDao.updateCategory(category, "None");
            mToDoDao.deleteTimer(category);
            return null;
        }
    }

    int getTaskCount(String category) {
        new ToDoRepository.GetCountAsyncTask(category).execute("getCount");
        SystemClock.sleep(30);
        return count;
    }

    private static int count;

    public static class GetCountAsyncTask extends AsyncTask<String, Void, Integer> {
        String category;

        GetCountAsyncTask(String category) {
            this.category = category;
        }

        @Override
        protected Integer doInBackground(String... strings) {
            count = mToDoDao.countFinishedTasks(category);
            return null;
        }
    }
}
