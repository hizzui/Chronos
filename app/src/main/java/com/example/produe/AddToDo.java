package com.example.produe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.produe.data.TimerEntity;
import com.example.produe.data.ToDoViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddToDo extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";
    private static final int NEW_TODO_ACTIVITY_REQUEST_CODE = 1;

    private EditText editedTaskTitle;
    private Button editedDate;
    private Button editedTime;
    private Spinner editedCategorySpinner;

    private ToDoViewModel toDoViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set view
        setContentView(R.layout.activity_add_todo);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.add_todo_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.create_todo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get view model
        toDoViewModel = new ViewModelProvider(this).get(ToDoViewModel.class);

        // Find all relevant view that has user inputs
        editedTaskTitle = findViewById(R.id.task_title);
        editedDate = findViewById(R.id.date_picker);
        editedTime = findViewById(R.id.time_picker);
        editedCategorySpinner = findViewById(R.id.spinner_category);

        ImageView addCategory = findViewById(R.id.addCategory);
        // Set on click listener for the image view for add category
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });

        // Setup the spinner
        setupSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu
        getMenuInflater().inflate(R.menu.add_todo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Handle what happens when the save button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Respond to a click on the "Save" menu option
        if (id == R.id.save_button) {
            // Save the user input
            saveData();
            // Close activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        Intent replyIntent = new Intent();

        // Check that task title is not empty
        if (TextUtils.isEmpty(editedTaskTitle.getText())) {
            // If task title is empty sent result cancelled
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            // Read from the input fields
            // Using trim to eliminate leading or trailing white space
            String taskTitle1 = editedTaskTitle.getText().toString().trim();
            taskTitle1 += "<>" + editedCategorySpinner.getSelectedItem().toString();
            if (!editedDate.getText().toString().equals("Pick Date")) {
                taskTitle1 += "<>" + editedDate.getText().toString();

                if (!editedTime.getText().toString().equals("Pick Time")) {
                    taskTitle1 += "<>" + editedTime.getText().toString();
                }
            }

            // Send data
            replyIntent.putExtra(EXTRA_REPLY, taskTitle1);
            setResult(RESULT_OK, replyIntent);
        }
    }

    private void setupSpinner() {
        List<String> categoryList = new ArrayList<String>();

        // Add "None to the category list
        categoryList.add("None");
        
        toDoViewModel.getCategories().observe(this, new Observer<List<TimerEntity>>() {
            @Override
            public void onChanged(List<TimerEntity> categories) {
                // Clear the category list
                categoryList.clear();
                // Add "None to the category list
                categoryList.add("None");
                for (TimerEntity category : categories) {
                    // Add categories to the list
                    categoryList.add(category.mCategory);
                }
            }
        });

        // Set adapter for the spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, categoryList);
        editedCategorySpinner.setAdapter(spinnerAdapter);
    }

    // Show Time Picker Dialog
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    // Show Date Picker Dialog
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void addCategory() {
        Intent intent = new Intent(this, AddTimer.class);
        startActivityForResult(intent, NEW_TODO_ACTIVITY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TODO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get data and split it by the delimiter ":"
            String[] sentString = Objects.requireNonNull(data.getStringExtra(AddTimer.EXTRA_REPLY)).split(":");
            String category = sentString[0];
            int color = Integer.parseInt(sentString[1]);
            // Create a new Timer Entity object with the data
            TimerEntity timerEntity = new TimerEntity(category, color,  0, 0, 0);

            // Insert the new Timer entity object
            toDoViewModel.insert(timerEntity);
            // Make a toast to let the user know, that the timer has been saved
            Toast.makeText(getApplicationContext(), "It has been saved", Toast.LENGTH_LONG).show();
        } else {
            // If there is no timer title
            // Make a toast to let the user know, that the task has not been saved
            Toast.makeText(getApplicationContext(), R.string.empty_not_saved, Toast.LENGTH_LONG).show();
        }
    }
}
