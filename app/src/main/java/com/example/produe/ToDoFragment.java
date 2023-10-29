package com.example.produe;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.produe.data.TimerEntity;
import com.example.produe.data.ToDoEntity;
import com.example.produe.data.ToDoViewModel;
import com.example.produe.widget.ToDoWidgetProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;

public class ToDoFragment extends Fragment {

    private static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;

    private ToDoViewModel mToDoViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate view
        View view = inflater.inflate(R.layout.todo_fragment, container, false);

        // Get recycler view
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_todo);

        // Set the adapter for the recycler view
        final ToDoListAdapter adapter = new ToDoListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Get ViewModel from the ViewModelProvider
        mToDoViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ToDoViewModel.class);

        // Add observer for the LiveData on categories
        // Make map to link category to color
        HashMap<String, Integer> colorLink = new HashMap<String, Integer>();
        mToDoViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<TimerEntity>>() {
            @Override
            public void onChanged(List<TimerEntity> timerEntities) {
                // Clear map
                colorLink.clear();

                // Fill map
                for (int i = 0; i < timerEntities.size(); i++) {
                    colorLink.put(timerEntities.get(i).mCategory, timerEntities.get(i).mColor);
                }
            }
        });

        // Add observer for the LiveData on tasks
        mToDoViewModel.getAllTasks().observe(getViewLifecycleOwner(), new Observer<List<ToDoEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ToDoEntity> toDoEntities) {
                // Set the tasks in the recycler view via the adapter
                adapter.setTasks(toDoEntities, false, colorLink);

                // Send out a broadcast to ToDoWidgetProvider class to update the widget when data is changed
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.setComponent(new ComponentName(Objects.requireNonNull(getContext()), ToDoWidgetProvider.class));
                getContext().sendBroadcast(intent);
            }
        });

        // Find the FAB
        FloatingActionButton fab = view.findViewById(R.id.fab);

        // Set what happens when the FAB is clicked
        fab.setOnClickListener(view1 -> {
            // Start AddToDo activity
            Intent intent = new Intent(getActivity(), AddToDo.class);
            startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
        });

        // Set has menu options as true
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear the current menu
        menu.clear();

        // Inflate the desired menu
        inflater.inflate(R.menu.todo_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // Getting result from activities
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that nothing went wrong with the sent result
        if (requestCode == NEW_TODO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // If the result is okay
            // Get the sent string
            String task = data.getStringExtra(AddToDo.EXTRA_REPLY);
            ToDoEntity toDoEntity;

            // Check number of delimiters and split the string accordingly
            if (task.length() - 6 == task.replace("<>", "").length()) {
                String[] todoArray = task.split("<>");
                String taskTitle = todoArray[0];
                String category = todoArray[1];
                String[] date = todoArray[2].split("/");
                int dateDay = Integer.parseInt(date[0]);
                int dateMonth = Integer.parseInt(date[1]);
                int dateYear = Integer.parseInt(date[2]);
                String[] time = todoArray[3].split(":");
                int timeHour = Integer.parseInt(time[0]);
                int timeMinute = Integer.parseInt(time[1]);

                // Create a new ToDoEntity object
                toDoEntity = new ToDoEntity(taskTitle, category, dateYear, dateMonth, dateDay, timeHour, timeMinute);

            } else if (task.length() - 4 == task.replace("<>", "").length()) {
                String[] todoArray = task.split("<>");
                String taskTitle = todoArray[0];
                String category = todoArray[1];
                String[] date = todoArray[2].split("/");
                int dateDay = Integer.parseInt(date[0]);
                int dateMonth = Integer.parseInt(date[1]);
                int dateYear = Integer.parseInt(date[2]);

                // Create a new ToDoEntity object
                toDoEntity = new ToDoEntity(taskTitle, category, dateYear, dateMonth, dateDay);

            } else {
                String[] todoArray = task.split("<>");
                String taskTitle = todoArray[0];
                String category = todoArray[1];

                // Create a new ToDoEntity object
                toDoEntity = new ToDoEntity(taskTitle, category);
            }

            // Insert a new row in the todo_table from the database
            mToDoViewModel.insert(toDoEntity);

            // Make a toast to make the user know that the task has been saved
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), R.string.saved, Toast.LENGTH_LONG).show();

        } else if (resultCode != RESULT_FIRST_USER) {
            // If the result is not okay - If there was no task title
            // Make a toast to make the user know that the task was not saved
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), R.string.empty_not_saved, Toast.LENGTH_LONG).show();
        }
    }

    // Handle what happens when the save button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Respond to a click on the "Save" menu option
        if (id == R.id.finishedToDos) {
            // Start new activity
            // Open the FinishedToDoActivity
            Intent intent = new Intent(getActivity(), FinishedToDoActivity.class);
            startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
