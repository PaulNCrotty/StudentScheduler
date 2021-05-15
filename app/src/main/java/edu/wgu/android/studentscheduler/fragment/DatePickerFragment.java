package edu.wgu.android.studentscheduler.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

import edu.wgu.android.studentscheduler.util.DateTimeUtil;


public class DatePickerFragment extends DialogFragment {

    private EditText selectedView;

    public DatePickerFragment(EditText selectedView) {
        this.selectedView = selectedView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedView.setText(DateTimeUtil.getDateString(year, month, dayOfMonth));
            }
        }, year, month, day);

    }

}
