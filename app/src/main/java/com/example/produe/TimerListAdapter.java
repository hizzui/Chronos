package com.example.produe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.produe.data.TimerEntity;


import java.util.List;

public class TimerListAdapter extends RecyclerView.Adapter<TimerListAdapter.TimerViewHolder> {

    static class TimerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView categoryTextView;

        private final Chronometer chronometerView;

        private final ImageButton timerButton;

        private final TextView totalTimeUsedTextView;

        private OnNoteListener onNoteListener;

        // Create view holder
        private TimerViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            // Find relevant views
            categoryTextView = itemView.findViewById(R.id.timerCategory);
            chronometerView = itemView.findViewById(R.id.timer);
            timerButton = itemView.findViewById(R.id.timerButton);
            totalTimeUsedTextView = itemView.findViewById(R.id.totalTimeUsed);
            this.onNoteListener = onNoteListener;

            // Set on click listener for the timer button
            timerButton.setOnClickListener(this);
        }

        // When the timer button has been clicked
        @Override
        public void onClick(View v) {
            // Call on note click with the relevant views as parameters
            onNoteListener.onNoteClick(getAdapterPosition(), categoryTextView, chronometerView, timerButton, totalTimeUsedTextView);
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position, TextView categoryTextView, Chronometer chronometerView, ImageButton timerButton, TextView totalTimeUsedTextView);
    }

    private final LayoutInflater mInflater;

    // Cached copy of words
    private List<TimerEntity> mCategories;

    private String categoryRunning;
    private boolean timerRunning;
    private long timerBase;

    private Context context;

    private OnNoteListener onNoteListener;

    // Constructor
    TimerListAdapter(Context context, OnNoteListener onNoteListener) {
        mInflater = LayoutInflater.from(context);
        this.context = context;

        this.onNoteListener = onNoteListener;
    }

    // Called when the view is created
    @NonNull
    @Override
    public TimerListAdapter.TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate view
        View itemView = mInflater.inflate(R.layout.recyclerview_timers, parent, false);

        // Find relevant views
        ImageButton editorView = itemView.findViewById(R.id.editorTimerView);
        TextView categoryText = itemView.findViewById(R.id.timerCategory);

        // Set on click for tge editor view
        editorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start a dialog fragment when clicked
                // Open the Editor fragment
                DialogFragment newFragment = new EditorFragment(false, context, categoryText.getText(), Integer.parseInt(categoryText.getContentDescription().toString()));
                newFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "timerEditor");
            }
        });

        return new TimerViewHolder(itemView, onNoteListener);
    }

    // On bind view holder
    @Override
    public void onBindViewHolder(@NonNull TimerListAdapter.TimerViewHolder holder, int position) {
        // Check that there is categories
        if (mCategories != null) {
            holder.setIsRecyclable(false);

            // Get the current object via the position
            TimerEntity current = mCategories.get(position);

            // Set the text in the relevant views
            holder.categoryTextView.setText(current.mCategory);
            holder.categoryTextView.setTextColor(current.mColor);
            holder.categoryTextView.setContentDescription("" + current.mColor);

            // Set deadline by concatenating the times from the deadline into one string
            String totalTimeUsed = current.mTimeElapsedHours + ":";

            // check that there is two numbers in the minutes
            if (current.mTimeElapsedMinutes < 10) {
                // If there is only one number, add a '0'
                totalTimeUsed += "0";
            }

            totalTimeUsed += current.mTimeElapsedMinutes + ":";

            // check that there is two numbers in the seconds
            if (current.mTimeElapsedSeconds < 10) {
                // If there is only one number, add a '0'
                totalTimeUsed += "0";
            }

            totalTimeUsed += current.mTimeElapsedSeconds;

            // Set the deadline text
            holder.totalTimeUsedTextView.setText(totalTimeUsed);

            // Check if the timer has been set to run
            if (timerRunning) {
                // Find for which category it has been set to run
                if (current.mCategory.equals(categoryRunning)) {
                    // Set the timer to run
                    holder.timerButton.setContentDescription("Stop Timer");
                    holder.timerButton.setImageResource(R.mipmap.stop_button);
                    holder.chronometerView.setBase(timerBase);
                    holder.chronometerView.start();
                }
            }

        } else {
            // Covers the case of data not being ready yet
            holder.categoryTextView.setText(R.string.no_categories);
        }
    }

    // Set the relevant variables
    void setCategories(List<TimerEntity> toDoEntities, String categoryRunning, boolean timerRunning, long timerBase) {
        mCategories = toDoEntities;
        notifyDataSetChanged();
        this.categoryRunning = categoryRunning;
        this.timerRunning = timerRunning;
        this.timerBase = timerBase;
    }

    // Count how many categories there is
    @Override
    public int getItemCount() {
        if (mCategories != null) {
            return mCategories.size();
        } else {
            return 0;
        }
    }
}
