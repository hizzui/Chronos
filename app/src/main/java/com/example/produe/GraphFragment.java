package com.example.produe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.produe.data.TimerEntity;
import com.example.produe.data.ToDoEntity;
import com.example.produe.data.ToDoViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class GraphFragment extends Fragment {

    private PieChart pieChart;

    private ToDoViewModel mToDoViewModel;
    private static List<TimerEntity> timerCategories;
    private static int countOpen;

    private static int countAll;
    private static int previousI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate view
        View view = inflater.inflate(R.layout.graph_fragment, container, false);

        // Set all static variables to 0 when view is created
        countOpen = 0;
        countAll = 0;
        previousI = 0;

        // Find chart view
        pieChart = view.findViewById(R.id.pieChartView);

        // Get ViewModel from the ViewModelProvider
        mToDoViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ToDoViewModel.class);

        // Add observer for the LiveData from categories
        mToDoViewModel.getCategories().observe(getViewLifecycleOwner(), new Observer<List<TimerEntity>>() {
            @Override
            public void onChanged(@Nullable final List<TimerEntity> timerEntities) {
                // Set Graph values with the list of timer entity objects
                assert timerEntities != null;
                setGraphValues(timerEntities, view);
                // Set timer categories
                timerCategories = timerEntities;
            }
        });

        // Add observer for the LiveData from finished tasks
        mToDoViewModel.getAllFinishedTasks().observe(getViewLifecycleOwner(), new Observer<List<ToDoEntity>>() {
            @Override
            public void onChanged(List<ToDoEntity> toDoEntities) {
                ++countOpen;
                // Call setNumberOfTasksFinished every time a new task has been finished
                setNumberOfTasksFinished(timerCategories, view);
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    private void setNumberOfTasksFinished(List<TimerEntity> timerEntities, View view) {
        // Find layout
        LinearLayout linearLayout = view.findViewById(R.id.graphLayout);

        // If the number of finished tasks changes but the onCreateView isn't called
        if (countOpen > 1) {
            // Set count all to 0 to restart counting
            countAll = 0;
        }

        boolean hasBeenInElse = false;

        // Iterate through the list of timer entity objects
        for (int i = 0; i < timerEntities.size(); i++) {

            // Check if this is the first time finished tasks has updated
            if (countOpen == 1) {
                // Make a new text view
                TextView textView = new TextView(getContext());

                // Get the number of finished tasks for the specific category
                int count = mToDoViewModel.getTaskCountCategory(timerEntities.get(i).mCategory);
                // Set the count and the category as text in the new text view
                textView.setText(timerEntities.get(i).mCategory + ": " + count);
                // Set i as the ID for the text view
                textView.setId(i);
                // Add the view
                linearLayout.addView(textView);
                // Add the count to the count of all finished tasks
                countAll += count;
                // Set the previous i
                previousI = i;
            } else {
                // If this is not the first time that finished tasks has been updated
                // Check if I is smaller than the last I that was in the if part of this if-else statement
                if (i <= previousI) {
                    // Find the text view that has the ID of the current i
                    TextView textView = view.findViewById(i);

                    // Get the new count of the finished tasks in the category
                    int count = mToDoViewModel.getTaskCountCategory(timerEntities.get(i).mCategory);
                    // Update the text in the text view
                    textView.setText(timerEntities.get(i).mCategory + ": " + count);
                    // Add the count to the count of all finished tasks
                    countAll += count;
                } else {
                    // It goes in here if a new category has been made and therefore the size of the list has grown
                    // Make a new text view
                    TextView textView = new TextView(getContext());

                    // Get the number of finished tasks for the specific category
                    int count = mToDoViewModel.getTaskCountCategory(timerEntities.get(i).mCategory);
                    // Set the count and the category as text in the new text view
                    textView.setText(timerEntities.get(i).mCategory + ": " + count);
                    // Set i as the ID for the text view
                    textView.setId(i);
                    // Add the view
                    linearLayout.addView(textView);
                    // Add the count to the count of all finished tasks
                    countAll += count;
                    // Set previousI to i - So that previousI will hold the last value of I (the previous last index of the list)
                    previousI = i;
                }
            }
        }

        // Add the count of finished tasks with no category to countAll
        countAll += mToDoViewModel.getTaskCountCategory("None");

        // Find and set the text view to all counted finished tasks
        TextView allCounted = view.findViewById(R.id.numberOfTotalTasksFinished);
        allCounted.setText("" + countAll);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Clear the menu
        menu.clear();

        // Inflate the desired menu (An empty menu)
        inflater.inflate(R.menu.finished_todo_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setGraphValues(List<TimerEntity> timerEntities, View view) {
        // Make arrays to store the data
        ArrayList timeSpent = new ArrayList();
        ArrayList<String> categories = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        float totalTimeUsed = 0;

        // Iterate through the list of timer entity objects
        for (int i = 0; i < timerEntities.size(); i++) {
            TimerEntity timerEntity = timerEntities.get(i);

            // Calculate the time elapsed in hours
            float timeElapsed = (float)((timerEntity.mTimeElapsedHours) + (timerEntity.mTimeElapsedMinutes / 60.0) + ((timerEntity.mTimeElapsedSeconds / 60.0) / 60.0));
            // Add the data to the arrays
            timeSpent.add(new Entry(timeElapsed, i));
            // Get the category
            categories.add(timerEntity.mCategory);
            // Get the color
            colors.add(timerEntity.mColor);

            // Add the time elapsed to the total time used
            totalTimeUsed += timeElapsed;
        }

        // Make a new dataset for the graph
        PieDataSet dataSet = new PieDataSet(timeSpent, "");
        // Set the colors of the set by the values from the array colors
        dataSet.setColors(colors);

        // Set the pie data to be the previous data set matched with the categories
        PieData pieData = new PieData(categories, dataSet);
        pieChart.setData(pieData);

        // Set animation of the graph to last 1.5 seconds
        pieChart.animateXY(1500, 1500);

        // Find the text view to set total number of hours used
        TextView textView = view.findViewById(R.id.totalHoursUsed);
        // Set the number to be of max two decimals places
        DecimalFormat rounding = new DecimalFormat("#.##");
        // Set the text
        textView.setText("" + rounding.format(totalTimeUsed));
    }
}
