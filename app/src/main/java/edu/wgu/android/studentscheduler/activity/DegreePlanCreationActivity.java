package edu.wgu.android.studentscheduler.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import java.util.Calendar;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.fragment.DatePickerFragment;

public class DegreePlanCreationActivity extends StudentSchedulerActivity {
    public DegreePlanCreationActivity() {
        super(R.layout.activity_new_degreeplan_creation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_appbar_menu, menu);
        return true;
    }

    public void verifyAndSubmitPlan(View view) {
        String planName = getEditTextValue(R.id.planNameEditText);
        String studentName = getEditTextValue(R.id.studentNameEditText);
        String termName = getEditTextValue(R.id.termNameEditText);
    }

}
