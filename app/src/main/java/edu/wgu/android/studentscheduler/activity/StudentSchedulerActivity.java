package edu.wgu.android.studentscheduler.activity;

import android.view.View;
import android.widget.EditText;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Calendar;

import edu.wgu.android.studentscheduler.fragment.ConfirmationDialogFragment;
import edu.wgu.android.studentscheduler.fragment.DatePickerFragment;

public class StudentSchedulerActivity extends AppCompatActivity implements ConfirmationDialogFragment.ConfirmationDialogListener {

    private Calendar selectedDate;

    StudentSchedulerActivity(@LayoutRes int id) {
        super(id);
    }

    /**
     *
     * @param view - the edit text view which calls the date picker fragment
     */
    public void showDatePickerDialog(View view) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        DatePickerFragment datePicker = new DatePickerFragment((EditText) view);
        datePicker.show(supportFragmentManager, "datePicker");
    }

    public void cancel(View view) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        ConfirmationDialogFragment confirmation = new ConfirmationDialogFragment();
        confirmation.show(supportFragmentManager, "cancelConfirmation");
    }

    public String getEditTextValue(@IdRes int id) {
        String value = null;
        View view = findViewById(id);
        if(view != null) {
            value = ((EditText) view).getText().toString();
        }
        return value;
    }

    @Override
    public void onPositive(DialogFragment dialog) {
        finish(); // close the activity
    }

//    @Override
//    public void onNegative(DialogFragment dialog) {
//
//    }

}
