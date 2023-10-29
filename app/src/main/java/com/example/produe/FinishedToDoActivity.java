package com.example.produe;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.produe.data.TimerEntity;
import com.example.produe.data.ToDoEntity;
import com.example.produe.data.ToDoViewModel;
import com.example.produe.widget.ToDoWidgetProvider;

import java.util.HashMap;
import java.util.List;

public class FinishedToDoActivity extends AppCompatActivity {

    private ToDoViewModel mToDoViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view
        setContentView(R.layout.activity_finished_todos);

        // Find the recycler view
        RecyclerView recyclerView = findViewById(R.id.recyclerview_todo_finished);

        // Set an adapter for the recycler view
        final ToDoListAdapter adapter = new ToDoListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup a toolbar
        Toolbar toolbar = findViewById(R.id.todoToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Finished To-Do's");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getColor(R.color.white));

        // Calculate padding in px that equates to 16dp
        int padding = (int) (16 * getResources().getDisplayMetrics().density + 0.5f);
        // Set padding to the recycler view
        recyclerView.setPadding(padding, 0, padding, 0);

        // Get ViewModel from the ViewModelProvider
        mToDoViewModel = new ViewModelProvider(this).get(ToDoViewModel.class);

        // Make map to link category to color
        HashMap<String, Integer> colorLink = new HashMap<String, Integer>();
        mToDoViewModel.getCategories().observe(this, new Observer<List<TimerEntity>>() {
            @Override
            public void onChanged(List<TimerEntity> timerEntities) {
                // Clear the map
                colorLink.clear();
                // Fill map
                for (int i = 0; i < timerEntities.size(); i++) {
                    colorLink.put(timerEntities.get(i).mCategory, timerEntities.get(i).mColor);
                }
            }
        });

        // Add observer for the LiveData
        mToDoViewModel.getAllFinishedTasks().observe(this, new Observer<List<ToDoEntity>>() {
            @Override
            public void onChanged(@Nullable final List<ToDoEntity> toDoEntities) {
                // Set tasks in the adapter for the recycler view
                adapter.setTasks(toDoEntities, true, colorLink);

                // Send out a broadcast to ToDoWidgetProvider class to update the widget when data is changed
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.setComponent(new ComponentName(getApplicationContext(), ToDoWidgetProvider.class));
                getApplicationContext().sendBroadcast(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the desired menu (empty menu)
        getMenuInflater().inflate(R.menu.finished_todo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
