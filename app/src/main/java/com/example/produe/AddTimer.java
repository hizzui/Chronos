package com.example.produe;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddTimer extends AppCompatActivity {

    private TextView editedCategory;

    private FloatingActionButton fab;

    private int chosenColor = R.color.black;

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set view
        setContentView(R.layout.activity_add_timer);

        // Find and setup toolbar
        Toolbar toolbar = findViewById(R.id.add_timer_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.create_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find all relevant view that has user inputs
        editedCategory = findViewById(R.id.category_name);
        fab = findViewById(R.id.fabTimer);

        // Set on click listener for the FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the color picker dialog
                ColorPicker colorPicker = new ColorPicker(AddTimer.this);
                colorPicker.show();

                // Set what happens when a color has been picked
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        // Set the FAB background color to the chosen color
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
            // Close the activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData() {
        Intent replyIntent = new Intent();

        if (TextUtils.isEmpty(editedCategory.getText())) {
            // Cancel the result if there is no text in the category edit text view
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            // Read from the input fields
            // Using trim to eliminate leading or trailing white space
            String categoryName = editedCategory.getText().toString().trim() + ":" + chosenColor;

            // Send the data
            replyIntent.putExtra(EXTRA_REPLY, categoryName);
            setResult(RESULT_OK, replyIntent);
        }
    }
}
