package com.example.produe;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;

// Used to choose time
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    // Called when a time has been set and "OK" is being pressed
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Button timePicker;
        // Set hour and minute to string variables
        String minuteText = Integer.toString(minute);
        String hourText = Integer.toString(hourOfDay);

        // Check that there is two numbers in hour
        if (hourOfDay < 10) {
            // If not, add a zero in front of the hour text
            hourText = 0 + hourText;
        }

        // Check that there is two numbers in minute
        if (minute < 10) {
            // If not, add a zero in front of the minute text
            minuteText = 0 + minuteText;
        }

        try {
            // Try to get the timePicker button object via getActivity (if it's called from an activity)
            timePicker = Objects.requireNonNull(getActivity()).findViewById(R.id.time_picker);
            // Set the text of the timePicker button as a concatenated string with hours- and minutes text separated by ":"
            timePicker.setText(hourText + ":" + minuteText);
        } catch (NullPointerException e) {
            e.printStackTrace();
            assert getParentFragment() != null;
            // Go in here if getActivity() throws a null pointer exception (it goes in here if it's called from a fragment)
            // Use getParentFragment() instead of getActivity()
            timePicker = Objects.requireNonNull(getParentFragment().getView()).findViewById(R.id.time_picker);
            timePicker.setText(hourText + ":" + minuteText);
        }
    }
}
