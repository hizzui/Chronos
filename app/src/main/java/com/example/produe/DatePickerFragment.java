package com.example.produe;

import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Objects;
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(Objects.requireNonNull(getActivity()),this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Months are one less than the "real" months, therefore + 1
        month += 1;
        Button datePicker;

        try {
            // Try to find view via get activity
            datePicker = getActivity().findViewById(R.id.date_picker);
            // Set text of the view
            datePicker.setText(dayOfMonth + "/" + month + "/" + year);
        } catch (NullPointerException e) {
            // If get activity returns null (because it's for a fragment)
            e.printStackTrace();
            // Find view via get parent fragment
            datePicker = getParentFragment().getView().findViewById(R.id.date_picker);
            // Set text of the view
            datePicker.setText(dayOfMonth + "/" + month + "/" + year);
        }
    }
}
