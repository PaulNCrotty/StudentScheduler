package edu.wgu.android.studentscheduler;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import edu.wgu.android.studentscheduler.fragment.DatePickerFragment;
import edu.wgu.android.studentscheduler.fragment.DegreePlanFragment;
import edu.wgu.android.studentscheduler.persistence.DegreePlanRepositoryManager;

/**
 * Generally, your fragment must be embedded within an AndroidX FragmentActivity to contribute a
 * portion of UI to that activity's layout. FragmentActivity is the base class for AppCompatActivity,
 * so if you're already subclassing AppCompatActivity to provide backward compatibility in your app,
 * then you do not need to change your activity base class.
 * (https://developer.android.com/guide/fragments/create#java)
 */
public class MainActivity extends AppCompatActivity {

    private static DegreePlanRepositoryManager repositoryManager;

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repositoryManager = DegreePlanRepositoryManager.getInstance(getApplicationContext());

        long rowId = repositoryManager.insertDegreePlan("Test Plan");
        Log.d("MAIN", "DEGREE_PLAN_ROW_ID: " + rowId);
        /*
         * In the previous example, note that the fragment transaction is only created when
         * savedInstanceState is null. This is to ensure that the fragment is added only once, when
         * the activity is first created. When a configuration change occurs and the activity is
         * recreated, savedInstanceState is no longer null, and the fragment does not need to be
         * added a second time, _as the fragment is automatically restored_ from the
         * savedInstanceState. (see https://developer.android.com/guide/fragments/create)
         *
         * Other really useful information: https://developer.android.com/guide/fragments/fragmentmanager
         */
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.degree_plan_fragment, DegreePlanFragment.class, null)
                    .setReorderingAllowed(true)
                    .commit();
        }

    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

}