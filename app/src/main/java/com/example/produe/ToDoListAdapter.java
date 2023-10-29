package com.example.produe;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.produe.data.ToDoEntity;
import com.example.produe.data.ToDoViewModel;

import java.util.HashMap;
import java.util.List;

// List adapter to fill the list view for the To Do list
public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder> {

    // Make view holder
    static class ToDoViewHolder extends RecyclerView.ViewHolder {

        private final TextView todoItemView;

        private final TextView todoDeadlineView;

        private final TextView todoCategoryView;

        private final CheckBox todoCheckBoxView;

        private ToDoViewHolder(View itemView) {
            super(itemView);
            todoItemView = itemView.findViewById(R.id.taskTextView);
            todoDeadlineView = itemView.findViewById(R.id.deadlineTextView);
            todoCategoryView = itemView.findViewById(R.id.categoryTextView);
            todoCheckBoxView = itemView.findViewById(R.id.checkbox);
        }
    }

    private final LayoutInflater mInflater;

    // Cached copy of words
    private List<ToDoEntity> mTasks;

    private Context mContext;

    private boolean mIsDone;

    private HashMap<String, Integer> mColorLink;

    // Constructor
    ToDoListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    // When the view holder is created
    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate view
        View itemView = mInflater.inflate(R.layout.recyclerview_task_items, parent, false);

        // Find relevant views
        CheckBox checkBox = itemView.findViewById(R.id.checkbox);
        TextView taskText = itemView.findViewById(R.id.taskTextView);
        TextView deadlineText = itemView.findViewById(R.id.deadlineTextView);
        TextView categoryText = itemView.findViewById(R.id.categoryTextView);
        ImageButton editorView = itemView.findViewById(R.id.editorTaskView);

        // Get view model
        ToDoViewModel toDoViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(ToDoViewModel.class);

        // Set on click listener on the checkbox
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check whether the box is being checked or unchecked
                if (checkBox.isChecked()) {

                    // If the checkbox is checked
                    // Set a line through the text
                    taskText.setPaintFlags(taskText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    deadlineText.setPaintFlags(deadlineText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    categoryText.setPaintFlags(categoryText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    // Set the task as done in the database
                    toDoViewModel.setIsDone(Integer.parseInt(taskText.getContentDescription().toString()), true);
                } else {

                    // If the checkbox is unchecked
                    // Remove the line through the text
                    taskText.setPaintFlags(taskText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    deadlineText.setPaintFlags(deadlineText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    categoryText.setPaintFlags(categoryText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                    // Set the task as not done in the database
                    toDoViewModel.setIsDone(Integer.parseInt(taskText.getContentDescription().toString()), false);
                }
            }
        });

        editorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new EditorFragment(true, mContext, taskText.getContentDescription().toString(), taskText.getText(), deadlineText.getText(), categoryText.getText());
                newFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "taskEditor");
            }
        });

        return new ToDoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder holder, int position) {
        if (mTasks != null) {
            holder.setIsRecyclable(false);
            ToDoEntity current = mTasks.get(position);
            holder.todoItemView.setContentDescription( "" + current.mID);

            holder.todoItemView.setText(current.getTaskTitle());

            if (mIsDone) {
                holder.todoItemView.setPaintFlags(holder.todoItemView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.todoCheckBoxView.setChecked(true);
            }

            if (!current.mCategory.equals("None")) {
                holder.todoCategoryView.setText(current.mCategory);
                holder.todoCategoryView.setTextColor(mColorLink.get(current.mCategory));

                if (mIsDone) {
                    holder.todoCategoryView.setPaintFlags(holder.todoCategoryView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
            if (current.mDateYear != 9999) {
                String deadlineDate = current.mDateDay + "/";
                deadlineDate += current.mDateMonth + "/";
                deadlineDate += current.mDateYear + " ";

                if (current.mTimeHour != 99) {
                    if (current.mTimeHour < 10) {
                        deadlineDate += 0;
                    }
                    deadlineDate += current.mTimeHour + ":";
                    if (current.mTimeMinute < 10) {
                        deadlineDate += 0;
                    }
                    deadlineDate += current.mTimeMinute;
                }
                holder.todoDeadlineView.setText(deadlineDate);

                if (mIsDone) {
                    holder.todoDeadlineView.setPaintFlags(holder.todoDeadlineView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            } else {
                holder.todoDeadlineView.setText("");
            }
        } else {
            // Covers the case of data not being ready yet
            holder.todoItemView.setText("No Task");
        }
    }

    void setTasks(List<ToDoEntity> toDoEntities, boolean isDone, HashMap<String, Integer> colorLink) {
        mTasks = toDoEntities;
        mIsDone = isDone;
        mColorLink = colorLink;
        notifyDataSetChanged();
    }

    // getItemCount is called many times , and when it's first called, mTasks has not been updated -> it's null
    @Override
    public int getItemCount() {
        if (mTasks != null) {
            return mTasks.size();
        } else {
            return 0;
        }
    }
}
