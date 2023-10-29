package com.example.produe.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.produe.R;

import java.util.ArrayList;
import java.util.List;

public class ToDoWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private List<String> mTasks = new ArrayList<>();
    private List<String> mCategories = new ArrayList<>();
    private List<String> mDeadlines = new ArrayList<>();
    private Context mContext;

    // Constructor
    public ToDoWidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        // Call FetchTasks do in background method
        new FetchTasks().execute();
    }

    @Override
    public void onDataSetChanged() {
        // Call FetchTasks do in background method
        new FetchTasks().execute();
    }

    public class FetchTasks extends AsyncTask<Void, Void, Cursor> {

        // Return a cursor to tasks
        @Override
        protected Cursor doInBackground(Void... params) {
            Cursor cursor;

            cursor = mContext.getContentResolver().query(Contract.PATH_TODOS_URI, null, null, null, Contract._ID + " DESC");

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            // Create array lists to store the data
            final ArrayList<String> tasks = new ArrayList<>();
            final ArrayList<String> categories = new ArrayList<>();
            final ArrayList<String> deadlines = new ArrayList<>();
            String deadline;

            // Iterate through cursor
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // Add task to task array list
                tasks.add(cursor.getString(1));
                // Add category to category array list
                categories.add(cursor.getString(8));

                // Set together a string that contains the deadline
                deadline = "";
                if (cursor.getInt(4) < 10) {
                    deadline += "0";
                }
                deadline += cursor.getInt(4) + "/";
                if (cursor.getInt(3) < 10) {
                    deadline += "0";
                }
                deadline += cursor.getInt(3) + "/";
                if (cursor.getInt(2) < 10) {
                    deadline += "0";
                }
                deadline += cursor.getInt(2) + " ";
                if (cursor.getInt(5) < 10) {
                    deadline += "0";
                }
                deadline += cursor.getInt(5) + ":";
                if (cursor.getInt(6) < 10) {
                    deadline += "0";
                }
                deadline += cursor.getInt(6);

                // Add deadline to the deadline array list
                deadlines.add(deadline);
            }

            // Close the cursor
            cursor.close();

            // Send the data to initData
            initData(tasks, categories, deadlines);
        }
    }

    @Override
    public void onDestroy() {
        // Do nothing
    }

    @Override
    public int getCount() {
        // Delay 0.5 seconds for the data from the Async thread to be ready
        // For mCollection to be set
        SystemClock.sleep(500);
        // Return the size of mTasks
        return mTasks.size();
    }

    // Set views
    @Override
    public RemoteViews getViewAt(int position) {

        // Set the collection items layout the be a remote view object
        RemoteViews view = new RemoteViews(mContext.getPackageName(),
                R.layout.collection_widget_list_item);

        // Set the task title
        view.setTextViewText(R.id.widgetItemTaskNameLabel, mTasks.get(position));

        // Set the category text view
        if (mCategories.get(position).equals("None")) {
            view.setTextViewText(R.id.categoryView, "");
        } else {
            view.setTextViewText(R.id.categoryView, mCategories.get(position));
        }

        // Set the deadline text view
        if (mDeadlines.get(position).equals("99/99/9999 99:99")) {
            view.setTextViewText(R.id.deadlineView, "");
        } else if (mDeadlines.get(position).substring(mDeadlines.get(position).length() - 5).equals("99:99"))  {
            view.setTextViewText(R.id.deadlineView, mDeadlines.get(position).substring(0, 10));
        }
        else {
            view.setTextViewText(R.id.deadlineView, mDeadlines.get(position));
        }

        // Return view
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData(ArrayList<String> tasks, ArrayList<String> categories, ArrayList<String> deadlines) {
        // Clear all the array lists
        mTasks.clear();
        mCategories.clear();
        mDeadlines.clear();

        // Set the array lists equal to the parameter array lists
        mTasks.addAll(tasks);
        mCategories.addAll(categories);
        mDeadlines.addAll(deadlines);
    }
}
