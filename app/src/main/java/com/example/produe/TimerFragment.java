package com.example.produe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.produe.data.TimerEntity;
import com.example.produe.data.ToDoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class TimerFragment extends Fragment implements TimerListAdapter.OnNoteListener {

    private static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;
    private ToDoViewModel mToDoViewModel;


    private List<TimerEntity> mCategories;

    private Intent intent;

    private int hours;
    private int minutes;
    private int seconds;
    private String category;
    private String chrono;
    private long timersBase;
    private boolean timerRunning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.timer_fragment, container, false);

        // Find the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_todo);

        // Set the adapter for the recycler view
        final TimerListAdapter adapter = new TimerListAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get ViewModel from the ViewModelProvider
        mToDoViewModel = new ViewModelProvider(getActivity()).get(ToDoViewModel.class);

        // Add observer for the LiveData on categories
        mToDoViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<TimerEntity>>() {
            @Override
            public void onChanged(@Nullable final List<TimerEntity> toDoEntities) {
                // Set categories in adapter
                adapter.setCategories(toDoEntities, category, timerRunning, timersBase);
                mCategories = toDoEntities;
            }
        });

        // Find FAB button
        FloatingActionButton fab = view.findViewById(R.id.fab);
        // Set on click listener on FAB button
        fab.setOnClickListener(view1 -> {
            // Open AddTimer activity when clicked
            Intent intent = new Intent(getActivity(), AddTimer.class);
            startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
        });

        intent = new Intent(getActivity(), TimerService.class);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear menu
        menu.clear();

        // Inflate desired menu (Empty menu)
        inflater.inflate(R.menu.finished_todo_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Save data when the app is stopped
    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Save which timer is running
        editor.putLong("timerBase", timersBase);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putString("categoryRunning", category);

        editor.apply();
    }

    // Get the saved data when starting
    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("preferences", Context.MODE_PRIVATE);

        timerRunning = preferences.getBoolean("timerRunning", false);

        // Check if a timer was running
        if (timerRunning) {
            // If a timer was running
            // Make the same timer continue to run
            timersBase = preferences.getLong("timerBase", 0);
            category = preferences.getString("categoryRunning", null);
        }
    }

    // Getting result from activities
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that nothing went wrong with the sent result
        if (requestCode == NEW_TODO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // If the result is okay
            // Get the sent string and split it into the correct variables
            String[] sentString = Objects.requireNonNull(data.getStringExtra(AddTimer.EXTRA_REPLY)).split(":");
            String category = sentString[0];
            int color = Integer.parseInt(sentString[1]);

            // Make a new Timer Entity object
            TimerEntity toDoEntity = new TimerEntity(category, color,  0, 0, 0);

            // Add a new row in the timer_table int the database
            mToDoViewModel.insert(toDoEntity);

            // Make a toast to make the user know that the category has been saved
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), R.string.saved, Toast.LENGTH_LONG).show();
        } else {
            // If the result is not okay - If there was no category title
            // Make a toast to make the user know that the category was not saved
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), R.string.empty_not_saved_timer, Toast.LENGTH_LONG).show();
        }
    }

    // Called when a timer button is clicked
    @Override
    public void onNoteClick(int position, TextView categoryTextView, Chronometer chronometerView, ImageButton timerButton, TextView totalTimeUsedTextView) {

        // Get the category for which the timer button was pressed
        TimerEntity current = mCategories.get(position);

        // Check whether the timer should be started or stopped - depending on the content description of the timer
        if (timerButton.getContentDescription().equals("Start Timer")) {
            // Check if another timer is running
            if (timerRunning) {
                // Make the user know that the timer can't be started
                Toast.makeText(getContext(), "Only one timer can run at a time", Toast.LENGTH_LONG).show();
            } else {
                // Start the timer
                timersBase = SystemClock.elapsedRealtime();
                chronometerView.setBase(timersBase);
                chronometerView.start();
                timerRunning = true;

                category = current.mCategory;

                // Start the service to run in the background
                startService();

                // Change the picture on the button
                timerButton.setImageResource(R.mipmap.stop_button);
                // Change the content description
                timerButton.setContentDescription("Stop Timer");
            }

        } else {

            hours = current.mTimeElapsedHours;
            minutes = current.mTimeElapsedMinutes;
            seconds = current.mTimeElapsedSeconds;
            // Get the text from the timer
            chrono = chronometerView.getText().toString();

            // Stop the timer
            chronometerView.stop();
            timerRunning = false;
            // Change the content description
            timerButton.setContentDescription("Start Timer");
            // Change the picture on the button
            timerButton.setImageResource(R.mipmap.start_button);

            // Stop the service
            stopService();

            // Save the elapsed time
            saveElapsedTime();

        }
    }

    // Start service
    private void startService() {
        Objects.requireNonNull(getActivity()).startService(intent);
    }

    // Stop service
    private void stopService() {
        Objects.requireNonNull(getActivity()).stopService(intent);
    }

    // Save the time from the timer
    private void saveElapsedTime() {

        int usedMinutes;
        int usedSeconds;

        // Check number of delimiters ":" to see if more than an hour has passed
        if (chrono.length() - 2 == chrono.replace(":", "").length()) {
            // More than an hour has passed
            String[] timerArray = chrono.split(":");
            int usedHours = Integer.parseInt(timerArray[0]);
            // Add number of hours to the current number of hours
            hours += usedHours;
            usedMinutes = Integer.parseInt(timerArray[1]);
            usedSeconds = Integer.parseInt(timerArray[2]);
        } else {
            // Less than an hour has passed
            String[] timerArray = chrono.split(":");
            usedMinutes = Integer.parseInt(timerArray[0]);
            usedSeconds = Integer.parseInt(timerArray[1]);
        }

        // Add number of seconds to the current number of seconds
        seconds += usedSeconds;

        // Check if there is more than 59 seconds
        if (seconds >= 60) {
            // There is more than 59 seconds
            // Round up to one minute
            seconds -= 60;
            minutes += 1;
        }

        // Add number of minutes to the current number of minutes
        minutes += usedMinutes;

        // Check if there is more than 59 minutes
        if (minutes >= 60) {
            // There is more than 59 minutes
            // Round up to one hour
            minutes -= 60;
            hours += 1;
        }

        // Update the used time in the database
        mToDoViewModel.setTimeUsed(category, hours, minutes, seconds);
    }
}
