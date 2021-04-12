package edu.wgu.android.studentscheduler.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int year;
        int month;
        int day;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
            day = now.getDayOfMonth();
        } else {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Toast.makeText(getActivity(), String.format("You chose %d-%d-%d", year, month, dayOfMonth), Toast.LENGTH_LONG).show();
            }
        }, year, month, day);

    }

}
