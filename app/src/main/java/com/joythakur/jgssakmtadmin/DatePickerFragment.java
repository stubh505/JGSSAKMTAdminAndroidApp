package com.joythakur.jgssakmtadmin;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private SimpleDateFormat dateFormatterEntry = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
    private Calendar calendar;

    private DatePickedListener listener;

    public interface DatePickedListener {
        void onDatePicked(String date);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        listener = (DatePickedListener) getActivity();

        return new DatePickerDialog(requireActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        calendar.set(year, month, day, 0, 0);
        String formattedDate = dateFormatterEntry.format(calendar.getTime());
        //Log.i(TAG, formattedDate);
        listener.onDatePicked(formattedDate);
    }
}