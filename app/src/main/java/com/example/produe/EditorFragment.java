package com.example.produe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.produe.data.TimerEntity;
import com.example.produe.data.ToDoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class EditorFragment extends DialogFragment {
    private boolean isTask;
    private int id;
    private CharSequence taskTitle;
    private CharSequence deadlineText;
    private CharSequence categoryText;
    private ArrayAdapter<String> spinnerAdapter;

    private EditText editedTaskTitle;
    private Button editedDate;
    private Button editedTime;
    private Spinner editedCategorySpinner;

    private ToDoViewModel toDoViewModel;

    private Context context;

    private Button deleteButton;
    private Button editButton;

    private int chosenColor = R.color.black;

    private static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;

    // Constructor
    public EditorFragment(boolean isTask, Context context, String id, CharSequence taskText, CharSequence deadlineText, CharSequence categoryText) {
        this.isTask = isTask;
        this.context = context;
        this.id = Integer.parseInt(id);
        this.taskTitle = taskText;
        this.deadlineText = deadlineText;
        this.categoryText = categoryText;
    }

    // Constructor
    public EditorFragment(boolean isTask, Context context, CharSequence categoryText, int color) {
        this.isTask = isTask;
        this.context = context;
        this.categoryText = categoryText;
        this.chosenColor = color;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Check if it's the task view that called editor
        if (isTask) {
            // Inflate view
            View view = inflater.inflate(R.layout.activity_add_todo, container, false);

            // Find toolbar
            Toolbar toolbar = view.findViewById(R.id.add_todo_toolbar);
            // Set the title of the toolbar
            toolbar.setTitle("Edit Task");

            // Get view model
            toDoViewModel = new ViewModelProvider(this).get(ToDoViewModel.class);

            // Find all relevant views that has user inputs
            editedTaskTitle = view.findViewById(R.id.task_title);
            editedDate = view.findViewById(R.id.date_picker);
            editedTime = view.findViewById(R.id.time_picker);
            editedCategorySpinner = view.findViewById(R.id.spinner_category);

            // Set on click listener for edit date
            editedDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog();
                }
            });

            // Set on click listener for edit time
            editedTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePickerDialog();
                }
            });

            // Setup the spinner
            setupSpinner();

            // Setup buttons
            setButtons(view);

            // Fill view with current data
            editedTaskTitle.setText(taskTitle);
            if(deadlineText.length() > 2) {
                editedDate.setText(deadlineText.subSequence(0, 9));
                if (deadlineText.length() > 12) {
                    editedTime.setText(deadlineText.subSequence(9, deadlineText.length()));
                }
            }

            // Set the start item on the spinner with the previously chosen category
            editedCategorySpinner.post(new Runnable() {
                @Override
                public void run() {
                    editedCategorySpinner.setSelection(spinnerAdapter.getPosition(categoryText.toString()));
                }
            });

            // Watch for clicks on edit button
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the edit view is clicked
                    int day;
                    int month;
                    int year;
                    int hour;
                    int minute;

                    // Get data from the date button
                    if (!editedDate.getText().toString().equals("Pick Date")) {
                        String[] dateArray = editedDate.getText().toString().trim().split("/");
                        day = Integer.parseInt(dateArray[0]);
                        month = Integer.parseInt(dateArray[1]);
                        year = Integer.parseInt(dateArray[2]);
                    } else {
                        day = 99;
                        month = 99;
                        year = 99;
                    }

                    // Get data from the time button
                    if (!editedTime.getText().toString().equals("Pick Time")) {
                        String[] timeArray = editedTime.getText().toString().trim().split(":");
                        hour = Integer.parseInt(timeArray[0]);
                        minute = Integer.parseInt(timeArray[1]);
                    } else {
                        hour = 99;
                        minute = 99;
                    }

                    // Update the task
                    toDoViewModel.updateTask(id, editedTaskTitle.getText().toString().trim(), editedCategorySpinner.getSelectedItem().toString(), year, month, day, hour, minute);

                    // Close the dialog
                    dismiss();
                    // Make a toast to let the user know that the task has been saved
                    Toast.makeText(context, "Task saved", Toast.LENGTH_LONG).show();
                }
            });

            // Set on click listener for the delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the delete view is clicked
                    // Delete the task
                    toDoViewModel.deleteTask(id);

                    // Close the dialog
                    dismiss();
                    // Make a toast to let the user know, that the task has been deleted
                    Toast.makeText(context, "Task deleted", Toast.LENGTH_LONG).show();
                }
            });

            // Find image view
            ImageView addCategory = view.findViewById(R.id.addCategory);
            // Set on click listener on the image view
            addCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCategory();
                }
            });

            // Return view
            return view;
        } else {
            // If it's not task that called (called from timer)
            // Inflate view
            View view = inflater.inflate(R.layout.activity_add_timer, container, false);

            // Get view model
            toDoViewModel = new ViewModelProvider(this).get(ToDoViewModel.class);

            // Find toolbar
            Toolbar toolbar = view.findViewById(R.id.add_timer_toolbar);
            // Set toolbar title
            toolbar.setTitle("Edit Task");

            // Find FAB
            FloatingActionButton fab = view.findViewById(R.id.fabTimer);
            // Set the color of the FAB to the previous color of the category
            fab.setBackgroundTintList(ColorStateList.valueOf(chosenColor));

            // Set on click listener for FAB
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open color picker dialog
                    ColorPicker colorPicker = new ColorPicker(Objects.requireNonNull(getActivity()));
                    colorPicker.show();

                    // What happens, when a color is chosen
                    colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                        @Override
                        public void onChooseColor(int position, int color) {
                            // Change the background color of the FAB to the chosen color
                            fab.setBackgroundTintList(ColorStateList.valueOf(color));
                            chosenColor = color;
                        }

                        @Override
                        public void onCancel() {
                            // Do nothing
                        }
                    });
                }
            });

            // Set buttons
            setButtons(view);

            // Fill view with current data
            EditText categoryView = view.findViewById(R.id.category_name);
            categoryView.setText(categoryText);

            // Watch for clicks on edit button
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the edit view is clicked
                    toDoViewModel.updateTimer(categoryText.toString(), categoryView.getText().toString(), chosenColor);

                    // Close the dialog
                    dismiss();

                    // Make a toast to let the user know that the timer has been saved
                    Toast.makeText(context, "Timer saved", Toast.LENGTH_LONG).show();
                }
            });

            // Set on click listener for the delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // When the delete view is clicked
                   // Delete the timer
                    toDoViewModel.deleteTimer(categoryText.toString());

                    // Close the dialog
                    dismiss();

                    // Make a toast to let the user know, that the timer has been deleted
                    Toast.makeText(context, "Timer deleted", Toast.LENGTH_LONG).show();
                }
            });

            // Return view
            return view;
        }
    }

    // Setup spinner
    private void setupSpinner() {
        List<String> categoryList = new ArrayList<String>();

        // Add "None" as the first element in the category list
        categoryList.add("None");

        // Observe LiveData on categories
        toDoViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<TimerEntity>>() {
            @Override
            public void onChanged(List<TimerEntity> categories) {
                for (TimerEntity category : categories) {
                    // Add categories to the list
                    categoryList.add(category.mCategory);
                }
            }
        });

        // Set adapter for the spinner
        spinnerAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, categoryList);
        editedCategorySpinner.setAdapter(spinnerAdapter);
    }

    // Show Time Picker Dialog
    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getChildFragmentManager(), "timePicker");
    }

    // Show Date Picker Dialog
    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getChildFragmentManager(), "datePicker");
    }

    // Set buttons
    private void setButtons(View view) {
        LinearLayout layout;

        // Find view depending on if it's for tasks of categories
        if (isTask) {
            layout = view.findViewById(R.id.addToDoLayout);
        } else {
            layout = view.findViewById(R.id.addTimerLayout);
        }

        // Add a new layout
        LinearLayout horizontalLayout = new LinearLayout(context);
        // Set orientation on the layout
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        // Create new buttons
        deleteButton = new Button (context);
        editButton = new Button (context);

        // Set background color of buttons
        deleteButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        editButton.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        // Define parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        params.gravity = Gravity.RIGHT;
        params.leftMargin = 16;
        params.rightMargin = 8;

        // Set parameters for edit button
        editButton.setLayoutParams(params);

        params.gravity = Gravity.LEFT;
        // Set parameters for delete button
        deleteButton.setLayoutParams(params);

        // Set button text
        editButton.setText(R.string.save);
        deleteButton.setText(R.string.delete);

        // Set the text color fo the buttons to white
        editButton.setTextColor(ContextCompat.getColor(context, R.color.white));
        deleteButton.setTextColor(ContextCompat.getColor(context, R.color.white));

        // Add the buttons to the new layout
        horizontalLayout.addView(deleteButton);
        horizontalLayout.addView(editButton);
        // Add the new layout to the XML made layout
        layout.addView(horizontalLayout);
    }

    // Add category
    private void addCategory() {
        Intent intent = new Intent(getContext(), AddTimer.class);
        startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
    }
}
