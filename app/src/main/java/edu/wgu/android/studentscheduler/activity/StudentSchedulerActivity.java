package edu.wgu.android.studentscheduler.activity;

import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.fragment.ConfirmationDialogFragment;
import edu.wgu.android.studentscheduler.fragment.DatePickerFragment;
import edu.wgu.android.studentscheduler.fragment.GeneralErrorDialogFragment;
import edu.wgu.android.studentscheduler.persistence.DegreePlanRepositoryManager;
import edu.wgu.android.studentscheduler.util.DateTimeUtil;

import static edu.wgu.android.studentscheduler.util.StringUtil.isEmpty;

public class StudentSchedulerActivity extends AppCompatActivity implements ConfirmationDialogFragment.ConfirmationDialogListener {

    public static final String DEGREE_PLAN_ID_BUNDLE_KEY = "edu.wgu.studentscheduler.activity.degreePlanId";

    final DegreePlanRepositoryManager repositoryManager = DegreePlanRepositoryManager.getInstance(this);

    int invalidEntryColor;
    int validEntryColor;


    StudentSchedulerActivity(@LayoutRes int id) {
        super(id);
    }

    /**
     * To initialize standard colors and other key values used to dynamically modify
     * styles or other views. Should only be called after or at the end of onCreate.
     */
    void init() {
        invalidEntryColor = getResources().getColor(R.color.red_orange);
        validEntryColor = getResources().getColor(R.color.white);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_appbar_menu, menu);
        return true;
    }

    public int getSecondsSinceEpoch(String dateString) {
        int secondsSinceEpoch = 0;
        try {
            secondsSinceEpoch = (int) DateTimeUtil.getSecondsSinceEpoch(dateString);
        } catch (java.text.ParseException pe) {
            String title = "INVALID DATE";
            String errorMessage = "There was an error trying to parse the date \"" + dateString + "\". Is it in the proper ISO8601 date format?";
            GeneralErrorDialogFragment errorDialog = new GeneralErrorDialogFragment(title, errorMessage);
            errorDialog.show(getSupportFragmentManager(), "dateError");
        }
        return secondsSinceEpoch;
    }

    /**
     * @param view - the edit text view which calls the date picker fragment
     */
    public void showDatePickerDialog(View view) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        DatePickerFragment datePicker = new DatePickerFragment((EditText) view);
        datePicker.show(supportFragmentManager, "datePicker");
    }

    public void cancel(View view) {
        boolean viewModified = isViewModified(view);

        if (viewModified) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            ConfirmationDialogFragment confirmation = new ConfirmationDialogFragment();
            confirmation.show(supportFragmentManager, "cancelConfirmation");
        } else {
            finish();
        }
    }

    boolean isViewModified(View view) {
        boolean viewModified = false;
        int i = 0;
        ViewGroup container = (ViewGroup) view.getParent();
        int childCount = container.getChildCount();
        do {
            View child = container.getChildAt(i++);
            if (child instanceof EditText) {
                viewModified = !isEmpty(((EditText) child).getText().toString());
            } else if (child instanceof RadioGroup) {
                int j = 0;
                RadioGroup radioGroup = (RadioGroup) child;
                int rButtonCount = radioGroup.getChildCount();
                do {
                    RadioButton rButton = (RadioButton) radioGroup.getChildAt(j++);
                    // the default button will be checked and also have a hint of 'default'; no other buttons will have a hint
                    viewModified = rButton.isChecked() && rButton.getHint() == null;
                } while (j < rButtonCount && !viewModified);
            }
        } while (i < childCount && !viewModified);
        return viewModified;
    }

    public String getEditTextValue(@IdRes int id) {
        String value = null;
        View view = findViewById(id);
        if (view != null) {
            value = ((EditText) view).getText().toString();
        }
        return value;
    }

    public String getRadioGroupSelection(@IdRes int id) {
        String selection = null;
        View view = findViewById(id);
        if (view instanceof RadioGroup) {
            RadioGroup radioGroup = (RadioGroup) view;
            int rButtonCount = radioGroup.getChildCount();
            for (int i = 0; i < rButtonCount; i++) {
                RadioButton rButton = (RadioButton) radioGroup.getChildAt(i);
                if (rButton.isChecked()) {
                    selection = rButton.getText().toString();
                }
            }
        }
        return selection;
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
