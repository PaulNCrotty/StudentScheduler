package edu.wgu.android.studentscheduler.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;

import java.util.HashSet;
import java.util.Set;

import edu.wgu.android.studentscheduler.R;
import edu.wgu.android.studentscheduler.fragment.GeneralErrorDialogFragment;
import edu.wgu.android.studentscheduler.fragment.MissingRequiredValueDialogFragment;

public class DegreePlanCreationActivity extends StudentSchedulerActivity {

    public DegreePlanCreationActivity() {
        super(R.layout.activity_new_degreeplan_creation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void verifyAndSubmitPlan(View view) {

        String studentName = getEditTextValue(R.id.studentNameEditText);
        String termStatus = getRadioGroupSelection(R.id.progressStatusSelectionGroup);

        Set<Integer> validValues = new HashSet<>();
        Set<Integer> invalidValues = new HashSet<>();
        String planName = getRequiredTextValue(R.id.planNameEditText, invalidValues, validValues);
        String termName = getRequiredTextValue(R.id.termNameEditText, invalidValues, validValues);
        long termStartDateSeconds = getRequiredDate(R.id.termStartDateEditText, invalidValues);
        long termEndDateSeconds = getRequiredDate(R.id.termEndDateEditText, invalidValues);
        verifyDates(termStartDateSeconds, termEndDateSeconds, invalidValues, validValues);

        if (invalidValues.size() > 0) {
            for (Integer id : invalidValues) {
                findViewById(id).setBackground(errorBorder);
            }

            // only reset background if there are still valid values; otherwise the form will be submitted
            // and the activity closes so the changes won't be noticeable
            for (Integer id : validValues) {
                findViewById(id).setBackground(null);
            }

            MissingRequiredValueDialogFragment missingDialog = new MissingRequiredValueDialogFragment();
            missingDialog.show(getSupportFragmentManager(), "missingRequiredValues");
        } else {
            long degreePlanId = repositoryManager.insertDegreePlan(planName, studentName);
            repositoryManager.insertTerm(degreePlanId, termName, termStartDateSeconds, termEndDateSeconds, termStatus.toUpperCase());
            Intent termListActivity = new Intent(getApplicationContext(), TermListActivity.class);
            termListActivity.putExtra(DEGREE_PLAN_ID_BUNDLE_KEY, degreePlanId);
            startActivity(termListActivity);
            finish();
        }

    }

    private void verifyDates(long startDateSeconds, long endDateSeconds,
                     Set<Integer> invalidValues, Set<Integer> validValues) {
        if (startDateSeconds != 0 && endDateSeconds != 0) {
            if (startDateSeconds > endDateSeconds) {
                invalidValues.add(R.id.termStartDateEditText);
                invalidValues.add(R.id.termEndDateEditText);
                String title = "INVALID TERM DATES";
                String message = "The term start date must be before the term end date";
                GeneralErrorDialogFragment errorDialog = new GeneralErrorDialogFragment(title, message);
                errorDialog.show(getSupportFragmentManager(), "dateErrors");
            } else {
                validValues.add(R.id.termStartDateEditText);
                validValues.add(R.id.termEndDateEditText);
            }
        }
    }

}
