package edu.wgu.android.studentscheduler.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.util.Calendar;

import edu.wgu.android.studentscheduler.util.DateTimeUtil;


public class DatePickerFragment extends DialogFragment {

    private EditText selectedView;
//    private DatePickerListener dialogListener;

    public DatePickerFragment(EditText selectedView) {
        this.selectedView = selectedView;
    }

//    public interface DatePickerListener {
//        void onDateSet(int year, int month, int dayOfMonth);
//    }
//
//    /**
//     * Ties the invoking activity or fragment (context) to this dialog, which allows the parent context
//     * to take its own actions (as defined in its implementation of onPositive and onNegative)
//     * in response to the user's choice from the dialog.
//     *
//     * @param context - the invoking activity or fragment (the parent context)
//     */
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        if (context instanceof DatePickerFragment.DatePickerListener) {
//            dialogListener = (DatePickerFragment.DatePickerListener) context;
//        } else {
//            throw new ClassCastException(context.toString() + " must implement DatePickerListener");
//        }
//    }

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
                selectedView.setText(DateTimeUtil.getDateString(year, month, dayOfMonth));
            }
        }, year, month, day);

    }

}
